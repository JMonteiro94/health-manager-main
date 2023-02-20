package com.myhealth.healthmanagermain.service.domain.impl;

import com.myhealth.healthmanagermain.bootstrap.RandomDataUtil;
import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.repository.UserAccountRepository;
import com.myhealth.healthmanagermain.security.SecurityUtils;
import com.myhealth.healthmanagermain.service.domain.UserAccountService;
import com.myhealth.healthmanagermain.service.dto.AdminUserDTO;
import com.myhealth.healthmanagermain.service.dto.UserAccountDTO;
import com.myhealth.healthmanagermain.web.rest.errors.InvalidPasswordException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

  @NonNull
  private final UserAccountRepository userRepository;
  @NonNull
  private final PasswordEncoder passwordEncoder;
  @NonNull
  private final CacheManager cacheManager;

  @Override
  @Transactional(readOnly = true)
  public Optional<UserAccount> getUserById(@NonNull Long id) {
    return userRepository.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserAccount> getUserByUsername(@NonNull String username) {
    return userRepository.findOneByUsername(username.toLowerCase(Locale.ENGLISH));
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserAccount> getUserByEmail(@NonNull String email) {
    return userRepository.findOneByEmailIgnoreCase(email);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<AdminUserDTO> getAllManagedUsers(@NonNull Pageable pageable) {
    return userRepository.findAll(pageable).map(AdminUserDTO::new);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserAccountDTO> getAllPublicUsers(@NonNull Pageable pageable) {
    return userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).map(UserAccountDTO::new);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserAccount> getUserWithAuthorities() {
    return SecurityUtils.getCurrentUserLogin()
        .flatMap(userRepository::findOneWithAuthoritiesByUsername);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserAccount> getUserWithAuthoritiesByEmail(@NonNull String email) {
    return userRepository
        .findOneWithAuthoritiesByEmailIgnoreCase(email);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserAccount> getUserWithAuthoritiesByUsername(@NonNull String username) {
    return userRepository
        .findOneWithAuthoritiesByUsername(username.toLowerCase(Locale.ENGLISH));
  }

  @Override
  @Transactional
  public void deleteByUsername(@NonNull String username) {
    getUserByUsername(username)
        .ifPresent(this::deleteUser);
  }

  @Override
  @Transactional
  public void deleteUser(@NonNull UserAccount userAccount) {
    userRepository.delete(userAccount);
    userRepository.flush();
    this.clearUserCaches(userAccount);
    log.debug("Deleted user: {}", userAccount.getUsername());
  }

  @Override
  @Transactional
  public void save(@NonNull UserAccount userAccount) {
    userRepository.save(userAccount);
  }

  @Override
  @Transactional
  public void saveAll(Collection<UserAccount> userAccount) {
    userRepository.saveAll(userAccount);
  }

  @Override
  @Transactional
  public void saveAndFlush(@NonNull UserAccount userAccount) {
    userRepository.saveAndFlush(userAccount);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserAccount> getUserByResetKey(@NonNull String key) {
    return userRepository.findOneByResetKey(key);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserAccount> getUserByActivationKey(@NonNull String key) {
    return userRepository.findOneByActivationKey(key);
  }

  @Override
  @Transactional
  public void activateUser(@NonNull UserAccount userAccount) {
    userAccount.setActivated(true);
    userAccount.setActivationKey(null);
    save(userAccount);
  }

  @Override
  @Transactional
  public void resetUserKey(@NonNull UserAccount userAccount) {
    userAccount.setResetKey(RandomDataUtil.generateResetKey());
    userAccount.setResetDate(Instant.now());
    save(userAccount);
  }

  @Override
  @Transactional
  public void resetUserPassword(@NonNull UserAccount userAccount, @NonNull String password) {
    userAccount.setPassword(passwordEncoder.encode(password));
    userAccount.setResetKey(null);
    userAccount.setResetDate(null);
    save(userAccount);
  }

  @Override
  @Transactional
  public void updateUserPassword(@NonNull UserAccount userAccount,
      @NonNull String oldNotEncryptedPassword,
      String newPassword) {
    String currentEncryptedPassword = userAccount.getPassword();
    if (!passwordEncoder.matches(oldNotEncryptedPassword, currentEncryptedPassword)) {
      throw new InvalidPasswordException();
    }
    String encryptedPassword = passwordEncoder.encode(newPassword);
    userAccount.setPassword(encryptedPassword);
    save(userAccount);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserAccount> getAllDeactivatedUsersCreatedBeforeXDays(int days) {
    return userRepository
        .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
            Instant.now().minus(days, ChronoUnit.DAYS));
  }

  @Override
  @Transactional
  public void deleteAll() {
    userRepository.deleteAll();
    userRepository.flush();
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserAccount> getAll() {
    return userRepository.findAll();
  }


  private void clearUserCaches(@NonNull UserAccount userAccount) {
    Objects.requireNonNull(cacheManager.getCache(UserAccountRepository.USERS_BY_USERNAME_CACHE))
        .evict(userAccount.getUsername());
    if (userAccount.getEmail() != null) {
      Objects.requireNonNull(cacheManager.getCache(UserAccountRepository.USERS_BY_EMAIL_CACHE))
          .evict(userAccount.getEmail());
    }
  }
}
