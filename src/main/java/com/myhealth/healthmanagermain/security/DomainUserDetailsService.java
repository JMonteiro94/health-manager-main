package com.myhealth.healthmanagermain.security;

import com.myhealth.healthmanagermain.aop.timer.MeasureTime;
import com.myhealth.healthmanagermain.domain.Authority;
import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.service.domain.UserAccountService;
import java.util.List;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

  @NonNull
  private final UserAccountService userAccountService;

  @Override
  @MeasureTime
  public UserDetails loadUserByUsername(final String username) {
    log.debug("Authenticating {}", username);
    if (new EmailValidator().isValid(username, null)) {
      return userAccountService.getUserWithAuthoritiesByEmail(username)
          .map(user -> createSpringSecurityUser(username, user))
          .orElseThrow(() -> new UsernameNotFoundException(
              String.format("User with email %s was not found in the database", username)));
    }

    return userAccountService.getUserWithAuthoritiesByUsername(username)
        .map(user -> createSpringSecurityUser(username, user))
        .orElseThrow(() -> new UsernameNotFoundException(
            String.format("User with username %s was not found in the database", username)));
  }

  private User createSpringSecurityUser(
      String username, UserAccount user) {
    String lowercaseUsername = username.toLowerCase(Locale.ENGLISH);
    if (!user.isActivated()) {
      throw new UserNotActivatedException(
          String.format("User %s was not activated", lowercaseUsername));
    }
    List<SimpleGrantedAuthority> grantedAuthorities = user
        .getAuthorities()
        .stream()
        .map(Authority::getName)
        .map(SimpleGrantedAuthority::new)
        .toList();
    return new User(user.getUsername(), user.getPassword(), grantedAuthorities);
  }
}
