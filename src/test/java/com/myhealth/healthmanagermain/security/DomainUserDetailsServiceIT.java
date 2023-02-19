package com.myhealth.healthmanagermain.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.myhealth.healthmanagermain.IntegrationTest;
import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.domain.enums.UserType;
import com.myhealth.healthmanagermain.service.domain.UserAccountService;
import java.time.LocalDate;
import java.util.Locale;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@IntegrationTest
class DomainUserDetailsServiceIT {

  private static final String USER_ONE_LOGIN = "one";
  private static final String USER_ONE_EMAIL = "one@localhost";
  private static final String USER_TWO_LOGIN = "two";
  private static final String USER_TWO_EMAIL = "two@localhost";
  private static final String USER_THREE_LOGIN = "three";
  private static final String USER_THREE_EMAIL = "three@localhost";

  @Autowired
  private UserAccountService userAccountService;

  @Autowired
  @Qualifier("userDetailsService")
  private UserDetailsService domainUserDetailsService;

  @BeforeEach
  public void init() {
    userAccountService.deleteAll();
    UserAccount userOne = new UserAccount();
    userOne.setUsername(USER_ONE_LOGIN);
    userOne.setPassword(RandomStringUtils.randomAlphanumeric(60));
    userOne.setActivated(true);
    userOne.setEmail(USER_ONE_EMAIL);
    userOne.setFirstName("userOne");
    userOne.setLastName("doe");
    userOne.setLangKey("en");
    userOne.setBirthDate(LocalDate.parse("2017-11-15"));
    userOne.setType(UserType.PRIVATE);
    userOne.setImageUrl("asd");
    userAccountService.save(userOne);

    UserAccount userTwo = new UserAccount();
    userTwo.setUsername(USER_TWO_LOGIN);
    userTwo.setPassword(RandomStringUtils.randomAlphanumeric(60));
    userTwo.setActivated(true);
    userTwo.setEmail(USER_TWO_EMAIL);
    userTwo.setFirstName("userTwo");
    userTwo.setLastName("doe");
    userTwo.setLangKey("en");
    userTwo.setBirthDate(LocalDate.parse("2017-11-15"));
    userTwo.setType(UserType.PRIVATE);
    userAccountService.save(userTwo);

    UserAccount userThree = new UserAccount();
    userThree.setUsername(USER_THREE_LOGIN);
    userThree.setPassword(RandomStringUtils.randomAlphanumeric(60));
    userThree.setActivated(false);
    userThree.setEmail(USER_THREE_EMAIL);
    userThree.setFirstName("userThree");
    userThree.setLastName("doe");
    userThree.setLangKey("en");
    userThree.setBirthDate(LocalDate.parse("2017-11-15"));
    userThree.setType(UserType.PRIVATE);
    userAccountService.save(userThree);
  }

  @Test
  void assertThatUserCanBeFoundByLogin() {
    UserDetails userDetails = domainUserDetailsService.loadUserByUsername(USER_ONE_LOGIN);
    assertThat(userDetails).isNotNull();
    assertThat(userDetails.getUsername()).isEqualTo(USER_ONE_LOGIN);
  }

  @Test
  void assertThatUserCanBeFoundByLoginIgnoreCase() {
    UserDetails userDetails = domainUserDetailsService.loadUserByUsername(
        USER_ONE_LOGIN.toUpperCase(Locale.ENGLISH));
    assertThat(userDetails).isNotNull();
    assertThat(userDetails.getUsername()).isEqualTo(USER_ONE_LOGIN);
  }

  @Test
  void assertThatUserCanBeFoundByEmail() {
    UserDetails userDetails = domainUserDetailsService.loadUserByUsername(USER_TWO_EMAIL);
    assertThat(userDetails).isNotNull();
    assertThat(userDetails.getUsername()).isEqualTo(USER_TWO_LOGIN);
  }

  @Test
  void assertThatUserCanBeFoundByEmailIgnoreCase() {
    UserDetails userDetails = domainUserDetailsService.loadUserByUsername(
        USER_TWO_EMAIL.toUpperCase(Locale.ENGLISH));
    assertThat(userDetails).isNotNull();
    assertThat(userDetails.getUsername()).isEqualTo(USER_TWO_LOGIN);
  }

  @Test
  void assertThatEmailIsPrioritizedOverLogin() {
    UserDetails userDetails = domainUserDetailsService.loadUserByUsername(USER_ONE_EMAIL);
    assertThat(userDetails).isNotNull();
    assertThat(userDetails.getUsername()).isEqualTo(USER_ONE_LOGIN);
  }

  @Test
  void assertThatUserNotActivatedExceptionIsThrownForNotActivatedUsers() {
    assertThatExceptionOfType(UserNotActivatedException.class)
        .isThrownBy(() -> domainUserDetailsService.loadUserByUsername(USER_THREE_LOGIN));
  }
}