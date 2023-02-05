package com.myhealth.healthmanagermain.service.domain.impl;

import com.myhealth.healthmanagermain.config.Constants;
import com.myhealth.healthmanagermain.domain.Authority;
import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.repository.AuthorityRepository;
import com.myhealth.healthmanagermain.repository.UserAccountRepository;
import com.myhealth.healthmanagermain.security.SecurityUtils;
import com.myhealth.healthmanagermain.service.domain.UserAccountService;
import com.myhealth.healthmanagermain.service.dto.UserAccountDTO;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
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

  private final UserAccountRepository userAccountRepository;
  private final AuthorityRepository authorityRepository;
  private final CacheManager cacheManager;
  private final PasswordEncoder passwordEncoder;


  public UserAccount createUser(UserAccountDTO userAccountDTO) {
    UserAccount userAccount = new UserAccount();
    userAccount.setUsername(userAccountDTO.getUsername().toLowerCase());
    userAccount.setFirstName(userAccountDTO.getFirstName());
    userAccount.setLastName(userAccountDTO.getLastName());
    userAccount.setEmail(userAccountDTO.getEmail().toLowerCase());
    userAccount.setImageUrl(userAccountDTO.getImageUrl());
    userAccount.setLangKey(userAccountDTO.getLangKey());
    userAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));
    userAccount.setActivated(true);
    if (userAccountDTO.getAuthorities() != null) {
      Set<Authority> authorities = userAccountDTO.getAuthorities().stream()
          .map(authorityRepository::findById)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toSet());
      userAccount.setAuthorities(authorities);
    }
    userAccountRepository.save(userAccount);
    this.clearUserCaches(userAccount);
    log.debug("Created Information for User: {}", userAccount);
    return userAccount;
  }

  @Override
  public void updateUser(String firstName, String lastName, String email, String langKey,
      String imageUrl) {
    SecurityUtils.getCurrentUserLogin()
        .flatMap(userAccountRepository::findOneByUsername)
        .ifPresent(user -> {
          user.setFirstName(firstName);
          user.setLastName(lastName);
          user.setEmail(email.toLowerCase());
          user.setLangKey(langKey);
          user.setImageUrl(imageUrl);
          this.clearUserCaches(user);
          log.debug("Changed Information for User: {}", user);
        });
  }

  @Override
  public Optional<UserAccountDTO> updateUser(UserAccountDTO userAccountDTO) {
    return Optional.of(userAccountRepository
            .findById(userAccountDTO.getId()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(user -> {
          this.clearUserCaches(user);
          user.setUsername(userAccountDTO.getUsername().toLowerCase());
          user.setFirstName(userAccountDTO.getFirstName());
          user.setLastName(userAccountDTO.getLastName());
          user.setEmail(userAccountDTO.getEmail().toLowerCase());
          user.setImageUrl(userAccountDTO.getImageUrl());
          user.setActivated(userAccountDTO.isActivated());
          user.setLangKey(userAccountDTO.getLangKey());
          Set<Authority> managedAuthorities = user.getAuthorities();
          managedAuthorities.clear();
          userAccountDTO.getAuthorities().stream()
              .map(authorityRepository::findById)
              .filter(Optional::isPresent)
              .map(Optional::get)
              .forEach(managedAuthorities::add);
          this.clearUserCaches(user);
          log.debug("Changed Information for User: {}", user);
          return user;
        })
        .map(UserAccountDTO::new);
  }

  @Override
  public void deleteUser(String login) {
    userAccountRepository.findOneByUsername(login).ifPresent(user -> {
      userAccountRepository.delete(user);
      this.clearUserCaches(user);
      log.debug("Deleted User: {}", user);
    });
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserAccountDTO> getAllManagedUsers(Pageable pageable) {
    return userAccountRepository.findAllByUsernameNot(pageable, Constants.ANONYMOUS_USER)
        .map(UserAccountDTO::new);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserAccount> getUserWithAuthoritiesByLogin(String login) {
    return userAccountRepository.findOneWithAuthoritiesByUsername(login);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserAccount> getUserWithAuthorities(Long id) {
    return userAccountRepository.findOneWithAuthoritiesById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserAccount> getUserWithAuthorities() {
    return SecurityUtils.getCurrentUserLogin()
        .flatMap(userAccountRepository::findOneWithAuthoritiesByUsername);
  }

  @Override
  @Transactional(readOnly = true)
  public List<String> getAuthorities() {
    return authorityRepository.findAll().stream().map(Authority::getName).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserAccount> getUserByEmail(String email) {
    return userAccountRepository.findOneByEmailIgnoreCase(email);
  }

  private void clearUserCaches(UserAccount userAccount) {
    Objects.requireNonNull(cacheManager.getCache(UserAccountRepository.USERS_BY_USERNAME_CACHE))
        .evict(userAccount.getUsername());
  }
}
