package com.myhealth.healthmanagermain.service.impl;

import com.myhealth.healthmanagermain.bootstrap.RandomDataUtil;
import com.myhealth.healthmanagermain.config.Constants;
import com.myhealth.healthmanagermain.domain.Authority;
import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.repository.UserAccountRepository;
import com.myhealth.healthmanagermain.security.AuthoritiesConstants;
import com.myhealth.healthmanagermain.security.SecurityUtils;
import com.myhealth.healthmanagermain.service.UserManagementService;
import com.myhealth.healthmanagermain.service.domain.AuthorityService;
import com.myhealth.healthmanagermain.service.domain.UserAccountService;
import com.myhealth.healthmanagermain.service.dto.AdminUserDTO;
import com.myhealth.healthmanagermain.web.rest.errors.EmailAlreadyUsedException;
import com.myhealth.healthmanagermain.web.rest.errors.ExpiredResetKey;
import com.myhealth.healthmanagermain.web.rest.errors.UsernameAlreadyUsedException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

  @NonNull
  private final UserAccountService userAccountService;
  @NonNull
  private final AuthorityService authorityService;
  @NonNull
  private final PasswordEncoder passwordEncoder;
  @NonNull
  private final CacheManager cacheManager;

  @Override
  public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {
    return Optional
        .of(userAccountService.getUserById(userDTO.getId()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(userAccount -> {
          this.clearUserCaches(userAccount);
          //TODO: move to a separate method
          userAccount.setUsername(userDTO.getUsername().toLowerCase());
          userAccount.setFirstName(userDTO.getFirstName());
          userAccount.setLastName(userDTO.getLastName());
          userAccount.setEmail(userDTO.getEmail().toLowerCase());
          userAccount.setImageUrl(userDTO.getImageUrl());
          userAccount.setActivated(userDTO.isActivated());
          userAccount.setLangKey(userDTO.getLangKey());
          userAccount.setType(userDTO.getType());
          userAccount.setBirthDate(userDTO.getBirthDate());
          Set<Authority> managedAuthorities = userAccount.getAuthorities();
          managedAuthorities.clear();
          userDTO
              .getAuthorities()
              .stream()
              .map(authorityService::findByName)
              .filter(Optional::isPresent)
              .map(Optional::get)
              .forEach(managedAuthorities::add);
          userAccountService.saveAndFlush(userAccount);
          this.clearUserCaches(userAccount);
          log.debug("Changed Information for userAccount: {}", userAccount);
          return userAccount;
        })
        .map(AdminUserDTO::new);
  }

  @Override
  public void updateUser(String firstName, String lastName, String email, String langKey,
      String imageUrl, LocalDate birthDate) {
    //TODO: change parameters to object
    SecurityUtils
        .getCurrentUserLogin()
        .flatMap(userAccountService::getUserByUsername)
        .ifPresent(userAccount -> {
          userAccount.setFirstName(firstName);
          userAccount.setLastName(lastName);
          if (email != null) {
            userAccount.setEmail(email.toLowerCase());
          }
          userAccount.setLangKey(langKey);
          userAccount.setImageUrl(imageUrl);
          userAccount.setBirthDate(birthDate);
          userAccountService.saveAndFlush(userAccount);
          this.clearUserCaches(userAccount);
          log.debug("Changed Information for userAccount: {}", userAccount);
        });
  }

  @Override
  public UserAccount createUser(AdminUserDTO userDTO) {
    //TODO: move to a separate method
    UserAccount userAccount = new UserAccount();
    userAccount.setUsername(userDTO.getUsername());
    userAccount.setFirstName(userDTO.getFirstName());
    userAccount.setLastName(userDTO.getLastName());
    if (userDTO.getEmail() != null) {
      userAccount.setEmail(userDTO.getEmail().toLowerCase());
    }
    userAccount.setImageUrl(userDTO.getImageUrl());
    if (userDTO.getLangKey() == null) {
      userAccount.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
    } else {
      userAccount.setLangKey(userDTO.getLangKey());
    }
    String encryptedPassword = passwordEncoder.encode(RandomDataUtil.generatePassword());
    userAccount.setPassword(encryptedPassword);
    userAccount.setResetKey(RandomDataUtil.generateResetKey());
    userAccount.setResetDate(Instant.now());
    userAccount.setActivated(true);
    userAccount.setType(userDTO.getType());
    userAccount.setBirthDate(userDTO.getBirthDate());
    Set<Authority> authorities = userDTO
        .getAuthorities()
        .stream()
        .map(authorityService::findByName)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toSet());
    userAccount.setAuthorities(authorities);
    userAccountService.save(userAccount);
    this.clearUserCaches(userAccount);
    log.debug("Created Information for userAccount: {}", userAccount);
    return userAccount;
  }

  @Override
  public Optional<UserAccount> activateRegistration(String key) {
    log.debug("Activating UserAccount for activation key {}", key);
    return userAccountService.getUserByActivationKey(key)
        .map(userAccount -> {
          userAccountService.activateUser(userAccount);
          this.clearUserCaches(userAccount);
          log.debug("Activated userAccount: {}", userAccount);
          return userAccount;
        });
  }

  @Override
  public UserAccount registerUser(AdminUserDTO userDTO, String password) {
    userAccountService.getUserByUsername(userDTO.getUsername())
        .ifPresent(existingUser -> {
          boolean removed = removeNonActivatedUser(existingUser);
          if (!removed) {
            throw new UsernameAlreadyUsedException();
          }
        });
    userAccountService.getUserByEmail(userDTO.getEmail())
        .ifPresent(existingUser -> {
          boolean removed = removeNonActivatedUser(existingUser);
          if (!removed) {
            throw new EmailAlreadyUsedException();
          }
        });
    //TODO: move to a separate method
    UserAccount newUser = new UserAccount();
    newUser.setUsername(userDTO.getUsername().toLowerCase());
    String encryptedPassword = passwordEncoder.encode(password);
    newUser.setPassword(encryptedPassword);
    newUser.setFirstName(userDTO.getFirstName());
    newUser.setLastName(userDTO.getLastName());
    if (userDTO.getEmail() != null) {
      newUser.setEmail(userDTO.getEmail().toLowerCase());
    }
    newUser.setImageUrl(userDTO.getImageUrl());
    newUser.setLangKey(userDTO.getLangKey());
    newUser.setActivated(false);
    newUser.setBirthDate(userDTO.getBirthDate());
    newUser.setType(userDTO.getType());
    newUser.setActivationKey(RandomDataUtil.generateActivationKey());
    Set<Authority> authorities = new HashSet<>();
    authorityService.findByName(AuthoritiesConstants.USER).ifPresent(authorities::add);
    newUser.setAuthorities(authorities);
    userAccountService.saveAndFlush(newUser);
    this.clearUserCaches(newUser);
    log.debug("Created Information for UserAccount: {}", newUser);
    return newUser;
  }

  @Override
  public Optional<UserAccount> requestPasswordReset(String email) {
    return userAccountService.getUserByEmail(email)
        .filter(UserAccount::isActivated)
        .map(userAccount -> {
          this.userAccountService.resetUserKey(userAccount);
          this.clearUserCaches(userAccount);
          return userAccount;
        });
  }

  @Override
  public Optional<UserAccount> completePasswordReset(String newPassword, String key) {
    log.debug("Reset UserAccount password for reset key {}", key);
    Optional<UserAccount> userAccountOptional = userAccountService.getUserByResetKey(key);
    if (userAccountOptional.isEmpty()) {
      return Optional.empty();
    }
    UserAccount userAccount = userAccountOptional.get();
    if (!userAccount.getResetDate().isAfter(Instant.now().minus(1, ChronoUnit.DAYS))) {
      throw new ExpiredResetKey();
    }
    this.userAccountService.resetUserPassword(userAccount, newPassword);
    this.clearUserCaches(userAccount);
    return Optional.of(userAccount);
  }

  @Override
  public void changeUserPassword(String oldNotEncryptedPassword, String newPassword) {
    SecurityUtils
        .getCurrentUserLogin()
        .flatMap(userAccountService::getUserByUsername)
        .ifPresent(userAccount -> {
          this.userAccountService.updateUserPassword(userAccount, oldNotEncryptedPassword,
              newPassword);
          this.clearUserCaches(userAccount);
          log.debug("Changed password for userAccount: {}", userAccount);
        });
  }

  private boolean removeNonActivatedUser(UserAccount existingUser) {
    if (existingUser.isActivated()) {
      return false;
    }
    userAccountService.deleteUser(existingUser);
    return true;
  }

  private void clearUserCaches(UserAccount userAccount) {
    Objects.requireNonNull(cacheManager.getCache(UserAccountRepository.USERS_BY_USERNAME_CACHE))
        .evict(userAccount.getUsername());
    Objects.requireNonNull(cacheManager.getCache(UserAccountRepository.USERS_BY_EMAIL_CACHE))
        .evict(userAccount.getEmail());
  }

  @Scheduled(cron = "0 0 1 * * ?")
  public void removeNotActivatedUsers() {
    this.userAccountService.getAllDeactivatedUsersCreatedBeforeXDays(3)
        .forEach(userAccount -> {
          log.debug("Deleting not activated user {}", userAccount.getUsername());
          this.userAccountService.deleteUser(userAccount);
          this.clearUserCaches(userAccount);
        });
  }
}
