package com.myhealth.healthmanagermain.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.myhealth.healthmanagermain.IntegrationTest;
import com.myhealth.healthmanagermain.bootstrap.RandomDataUtil;
import com.myhealth.healthmanagermain.domain.Authority;
import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.security.AuthoritiesConstants;
import com.myhealth.healthmanagermain.service.UserManagementService;
import com.myhealth.healthmanagermain.service.domain.AuthorityService;
import com.myhealth.healthmanagermain.service.domain.UserAccountService;
import com.myhealth.healthmanagermain.service.dto.AdminUserDTO;
import com.myhealth.healthmanagermain.service.dto.PasswordChangeDTO;
import com.myhealth.healthmanagermain.web.rest.dto.KeyAndPasswordDTO;
import com.myhealth.healthmanagermain.web.rest.dto.ManagedUserDTO;
import com.myhealth.healthmanagermain.web.rest.errors.ExpiredResetKey;
import com.myhealth.healthmanagermain.web.rest.errors.InvalidPasswordException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@WithMockUser(value = AccountControllerIT.TEST_USER_LOGIN)
@IntegrationTest
class AccountControllerIT {

  static final String TEST_USER_LOGIN = "test";

  @Autowired
  private UserManagementService userManagementService;

  @Autowired
  private AuthorityService authorityService;

  @Autowired
  private UserAccountService userAccountService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private MockMvc restAccountMockMvc;

  @BeforeEach
  void initAuthorities() {
    authorityService.saveAll(Set.of(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER));
  }

  @Test
  @WithUnauthenticatedMockUser
  @DisplayName("authenticate for unauthenticated user")
  void testNonAuthenticatedUser() throws Exception {
    restAccountMockMvc
        .perform(get("/api/authenticate").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(""));
  }

  @Test
  @DisplayName("authenticate for authenticated user")
  void testAuthenticatedUser() throws Exception {
    restAccountMockMvc
        .perform(
            get("/api/authenticate")
                .with(request -> {
                  request.setRemoteUser(TEST_USER_LOGIN);
                  return request;
                })
                .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(content().string(TEST_USER_LOGIN));
  }

  @Test
  @DisplayName("get existing account by security context login")
  void testGetExistingAccount() throws Exception {
    AdminUserDTO user = ControllerModelObjectsFixture.getValidAdminUserAccount();
    user.setUsername(TEST_USER_LOGIN);
    userManagementService.createUser(user);

    restAccountMockMvc
        .perform(get("/api/account").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.username").value(TEST_USER_LOGIN))
        .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
        .andExpect(jsonPath("$.lastName").value(user.getLastName()))
        .andExpect(jsonPath("$.email").value(user.getEmail()))
        .andExpect(jsonPath("$.imageUrl").value(user.getImageUrl()))
        .andExpect(jsonPath("$.langKey").value(user.getLangKey()))
        .andExpect(jsonPath("$.type").value(user.getType().toString()))
        .andExpect(jsonPath("$.birthDate").value(user.getBirthDate().toString()))
        .andExpect(jsonPath("$.authorities").isArray())
        .andExpect(jsonPath("$.authorities", hasSize(1)))
        .andExpect(jsonPath("$.authorities", hasItem(AuthoritiesConstants.ADMIN)));
  }

  @Test
  @DisplayName("fail to get unknown account")
  void testGetUnknownAccount() throws Exception {
    restAccountMockMvc
        .perform(get("/api/account").accept(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("register new account")
  void testRegisterValid() throws Exception {
    ManagedUserDTO validUser = ControllerModelObjectsFixture.getValidManagedUserVM();
    validUser.setUsername("valid-username");
    assertThat(userAccountService.getUserByUsername(validUser.getUsername())).isEmpty();

    restAccountMockMvc
        .perform(post("/api/register").contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(validUser)))
        .andExpect(status().isCreated());

    assertThat(userAccountService.getUserByUsername(validUser.getUsername())).isPresent();
  }

  @ParameterizedTest
  @MethodSource("invalidUsernameInputs")
  @DisplayName("fail to register account with invalid usernames")
  void testRegisterInvalidUsername(String username) throws Exception {
    ManagedUserDTO invalidUser = ControllerModelObjectsFixture.getValidManagedUserVM();
    invalidUser.setUsername(username);
    invalidUser.setEmail(RandomDataUtil.generateRandomEmailString());

    restAccountMockMvc
        .perform(post("/api/register").contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(invalidUser)))
        .andExpect(status().isBadRequest());

    Optional<UserAccount> user = userAccountService.getUserByEmail(invalidUser.getEmail());
    assertThat(user).isEmpty();
  }


  @ParameterizedTest
  @MethodSource("invalidEmailInputs")
  @DisplayName("fail to register account with invalid emails")
  void testRegisterInvalidEmail(String email) throws Exception {
    ManagedUserDTO invalidUser = ControllerModelObjectsFixture.getValidManagedUserVM();
    invalidUser.setEmail(email);

    performRegisterRequestAndExpectBadRequestAndUserWasNotCreated(invalidUser);
  }

  @ParameterizedTest
  @MethodSource("invalidPasswordInputs")
  @DisplayName("fail to register account with invalid passwords")
  void testRegisterInvalidPassword(String password) throws Exception {
    ManagedUserDTO invalidUser = ControllerModelObjectsFixture.getValidManagedUserVM();
    invalidUser.setPassword(password);

    performRegisterRequestAndExpectBadRequestAndUserWasNotCreated(invalidUser);
  }

  private void performRegisterRequestAndExpectBadRequestAndUserWasNotCreated(
      ManagedUserDTO invalidUser)
      throws Exception {
    restAccountMockMvc
        .perform(post("/api/register").contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(invalidUser)))
        .andExpect(status().isBadRequest());

    Optional<UserAccount> user = userAccountService.getUserByUsername(invalidUser.getUsername());
    assertThat(user).isEmpty();

  }

  @Test
  @DisplayName("fail to register new account with duplicate username")
  void testRegisterDuplicateUsername() throws Exception {
    ManagedUserDTO firstUser = ControllerModelObjectsFixture.getValidManagedUserVM();
    firstUser.setUsername("alice");
    firstUser.setEmail("alice@example.com");

    ManagedUserDTO secondUser = ControllerModelObjectsFixture.getValidManagedUserVM();
    secondUser.setUsername(firstUser.getUsername());
    secondUser.setPassword(firstUser.getPassword());
    secondUser.setFirstName(firstUser.getFirstName());
    secondUser.setLastName(firstUser.getLastName());
    secondUser.setEmail("alice2@example.com");
    secondUser.setImageUrl(firstUser.getImageUrl());
    secondUser.setLangKey(firstUser.getLangKey());
    secondUser.setAuthorities(new HashSet<>(firstUser.getAuthorities()));

    restAccountMockMvc
        .perform(post("/api/register").contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(firstUser)))
        .andExpect(status().isCreated());

    restAccountMockMvc
        .perform(post("/api/register").contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(secondUser)))
        .andExpect(status().isCreated());

    Optional<UserAccount> firsTestUser = userAccountService.getUserByEmail(firstUser.getEmail());
    assertThat(firsTestUser).isNotPresent();
    Optional<UserAccount> testUser = userAccountService.getUserByEmail(secondUser.getEmail());
    assertThat(testUser).isPresent();
    testUser.get().setActivated(true);
    userAccountService.save(testUser.get());

    restAccountMockMvc
        .perform(post("/api/register").contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(secondUser)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  @DisplayName("fail to register new account with duplicate email")
  void testRegisterDuplicateEmail() throws Exception {
    ManagedUserDTO firstUser = ControllerModelObjectsFixture.getValidManagedUserVM();
    firstUser.setUsername("test-register-duplicate-email");
    firstUser.setEmail("test-register-duplicate-email@example.com");

    restAccountMockMvc
        .perform(post("/api/register").contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(firstUser)))
        .andExpect(status().isCreated());

    Optional<UserAccount> testUser1 = userAccountService.getUserByUsername(firstUser.getUsername());
    assertThat(testUser1).isPresent();

    ManagedUserDTO secondUser = ControllerModelObjectsFixture.getValidManagedUserVM();
    secondUser.setUsername("test-register-duplicate-email-2");
    secondUser.setPassword(firstUser.getPassword());
    secondUser.setFirstName(firstUser.getFirstName());
    secondUser.setLastName(firstUser.getLastName());
    secondUser.setEmail(firstUser.getEmail());
    secondUser.setImageUrl(firstUser.getImageUrl());

    restAccountMockMvc
        .perform(post("/api/register").contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(secondUser)))
        .andExpect(status().isCreated());

    Optional<UserAccount> testUser2 = userAccountService.getUserByUsername(firstUser.getUsername());
    assertThat(testUser2).isEmpty();

    Optional<UserAccount> testUser3 = userAccountService.getUserByUsername(
        secondUser.getUsername());
    assertThat(testUser3).isPresent();

    ManagedUserDTO userWithUpperCaseEmail = ControllerModelObjectsFixture.getValidManagedUserVM();
    userWithUpperCaseEmail.setId(firstUser.getId());
    userWithUpperCaseEmail.setUsername("test-register-duplicate-email-3");
    userWithUpperCaseEmail.setPassword(firstUser.getPassword());
    userWithUpperCaseEmail.setFirstName(firstUser.getFirstName());
    userWithUpperCaseEmail.setLastName(firstUser.getLastName());
    userWithUpperCaseEmail.setEmail("TEST-register-duplicate-email@example.com");
    userWithUpperCaseEmail.setImageUrl(firstUser.getImageUrl());

    restAccountMockMvc
        .perform(
            post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(userWithUpperCaseEmail))
        )
        .andExpect(status().isCreated());

    Optional<UserAccount> testUser4 = userAccountService.getUserByUsername(
        userWithUpperCaseEmail.getUsername());
    assertThat(testUser4).isPresent();
    assertThat(testUser4.get().getEmail()).isEqualTo("test-register-duplicate-email@example.com");

    testUser4.get().setActivated(true);
    userManagementService.updateUser((new AdminUserDTO(testUser4.get())));

    restAccountMockMvc
        .perform(post("/api/register").contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(secondUser)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  @DisplayName("register new account with admin role and stored with use role")
  void testRegisterAdminIsIgnored() throws Exception {
    ManagedUserDTO validUser = ControllerModelObjectsFixture.getValidManagedUserVM();
    validUser.setAuthorities(Collections.singleton(AuthoritiesConstants.ADMIN));

    restAccountMockMvc
        .perform(post("/api/register").contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(validUser)))
        .andExpect(status().isCreated());

    Optional<UserAccount> userDup = userAccountService.getUserWithAuthoritiesByUsername(
        validUser.getUsername());
    assertThat(userDup).isPresent();
    assertThat(userDup.get().getAuthorities())
        .hasSize(1)
        .containsExactly(new Authority(AuthoritiesConstants.USER));
  }

  @Test
  @DisplayName("activate new account")
  void testActivateAccount() throws Exception {
    final String activationKey = "some activation key";
    UserAccount user = ControllerModelObjectsFixture.getValidUserAccount();
    user.setActivated(false);
    user.setActivationKey(activationKey);
    userAccountService.saveAndFlush(user);

    restAccountMockMvc.perform(get("/api/activate?key={activationKey}", activationKey))
        .andExpect(status().isOk());

    user = userAccountService.getUserByUsername(user.getUsername()).orElse(null);
    assertThat(user).isNotNull();
    assertThat(user.getId()).isPositive();
    assertThat(user.isActivated()).isTrue();
  }

  @Test
  @DisplayName("fail to activate new account with wrong key")
  void testActivateAccountWithWrongKey() throws Exception {
    restAccountMockMvc.perform(get("/api/activate?key=wrongActivationKey"))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser("save-account")
  @DisplayName("update new account")
  void testSaveAccount() throws Exception {
    UserAccount user = ControllerModelObjectsFixture.getValidUserAccount();
    user.setUsername("save-account");
    user.setEmail("save-account@example.com");
    userAccountService.saveAndFlush(user);

    AdminUserDTO userDTO = ControllerModelObjectsFixture.getValidAdminUserAccount();
    userDTO.setUsername(user.getUsername());
    userDTO.setEmail(user.getEmail());

    restAccountMockMvc
        .perform(post("/api/account").contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userDTO)))
        .andExpect(status().isOk());

    UserAccount updatedUser = userAccountService.getUserWithAuthoritiesByUsername(
        user.getUsername()).orElse(null);
    assertThat(updatedUser).isNotNull();
    assertThat(updatedUser.getId()).isPositive();
    assertThat(updatedUser.getFirstName()).isEqualTo(userDTO.getFirstName());
    assertThat(updatedUser.getLastName()).isEqualTo(userDTO.getLastName());
    assertThat(updatedUser.getEmail()).isEqualTo(userDTO.getEmail());
    assertThat(updatedUser.getLangKey()).isEqualTo(userDTO.getLangKey());
    assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
    assertThat(updatedUser.getImageUrl()).isEqualTo(userDTO.getImageUrl());
    assertThat(updatedUser.getBirthDate()).isEqualTo(userDTO.getBirthDate());
    assertThat(updatedUser.isActivated()).isTrue();
    assertThat(updatedUser.getAuthorities()).isNotEmpty();
    assertThat(updatedUser.getAuthorities()).hasSize(1);
  }

  @Test
  @WithMockUser("save-invalid-email")
  @DisplayName("fail to update account with invalid email")
  void testSaveInvalidEmail() throws Exception {
    //TODO: change to parameterized test
    UserAccount user = ControllerModelObjectsFixture.getValidUserAccount();
    user.setUsername("save-invalid-email");
    user.setEmail("save-invalid-email@example.com");

    userAccountService.saveAndFlush(user);

    AdminUserDTO userDTO = new AdminUserDTO(user);
    userDTO.setUsername("not-used");
    userDTO.setEmail("invalid email");
    userDTO.setActivated(false);

    restAccountMockMvc
        .perform(post("/api/account").contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userDTO)))
        .andExpect(status().isBadRequest());

    assertThat(userAccountService.getUserByEmail("invalid email")).isNotPresent();
  }

  @Test
  @WithMockUser("save-duplicated-email")
  @DisplayName("fail to update account with duplicated email")
  void testSaveExistingEmail() throws Exception {
    UserAccount user = ControllerModelObjectsFixture.getValidUserAccount();
    user.setUsername("save-duplicated-email");
    user.setEmail("save-duplicated-email@example.com");
    userAccountService.saveAndFlush(user);

    UserAccount anotherUser = ControllerModelObjectsFixture.getValidUserAccount();
    anotherUser.setUsername("save-duplicated-email2");
    anotherUser.setEmail("save-duplicated-email2@example.com");

    userAccountService.saveAndFlush(anotherUser);

    AdminUserDTO userDTO = new AdminUserDTO(user);
    userDTO.setUsername("not-used");
    userDTO.setEmail("save-duplicated-email2@example.com");

    restAccountMockMvc
        .perform(post("/api/account").contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userDTO)))
        .andExpect(status().isBadRequest());

    UserAccount updatedUser = userAccountService.getUserByUsername(user.getUsername())
        .orElse(null);
    assertThat(updatedUser).isNotNull();
    assertThat(updatedUser.getId()).isPositive();
    assertThat(updatedUser.getEmail()).isEqualTo(user.getEmail());

    UserAccount updatedAnotherUser = userAccountService.getUserByUsername(anotherUser.getUsername())
        .orElse(null);
    assertThat(updatedAnotherUser).isNotNull();
    assertThat(updatedAnotherUser.getId()).isPositive();
    assertThat(updatedAnotherUser.getEmail()).isEqualTo(anotherUser.getEmail());
  }

  @Test
  @WithMockUser("save-duplicated-email-and-login")
  @DisplayName("update account with different username but same email")
  void testSaveExistingEmailAndLogin() throws Exception {
    UserAccount user = ControllerModelObjectsFixture.getValidUserAccount();
    user.setUsername("save-duplicated-email-and-login");
    user.setEmail("save-duplicated-email-and-login@example.com");
    userAccountService.saveAndFlush(user);

    AdminUserDTO userDTO = ControllerModelObjectsFixture.getValidAdminUserAccount();
    userDTO.setUsername("not-used");
    userDTO.setEmail(user.getEmail());

    restAccountMockMvc
        .perform(post("/api/account").contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userDTO)))
        .andExpect(status().isOk());

    UserAccount updatedUser = userAccountService.getUserByUsername(user.getUsername())
        .orElse(null);
    assertThat(updatedUser).isNotNull();
    assertThat(updatedUser.getEmail()).isEqualTo(user.getEmail());
    assertThat(updatedUser.getFirstName()).isEqualTo(userDTO.getFirstName());
    assertThat(updatedUser.getLastName()).isEqualTo(userDTO.getLastName());
    assertThat(updatedUser.getBirthDate()).isEqualTo(userDTO.getBirthDate());
  }

  @Test
  @WithMockUser("change-password-wrong-existing-password")
  @DisplayName("fail to change password with wrong current password")
  void testChangePasswordWrongExistingPassword() throws Exception {
    UserAccount user = ControllerModelObjectsFixture.getValidUserAccount();
    String currentPassword = RandomDataUtil.generatePassword();
    user.setPassword(passwordEncoder.encode(currentPassword));
    user.setUsername("change-password-wrong-existing-password");
    userAccountService.saveAndFlush(user);

    final String newPassword = "new password";
    final String wrongPassword = "1" + currentPassword;
    restAccountMockMvc
        .perform(
            post("/api/account/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(
                    new PasswordChangeDTO(wrongPassword, newPassword)))
        )
        .andExpect(status().isBadRequest());

    UserAccount updatedUser = userAccountService.getUserByUsername(user.getUsername()).orElse(null);
    assertThat(updatedUser).isNotNull();
    assertThat(passwordEncoder.matches(newPassword, updatedUser.getPassword())).isFalse();
    assertThat(passwordEncoder.matches(currentPassword, updatedUser.getPassword())).isTrue();
  }

  @Test
  @WithMockUser("change-password")
  @DisplayName("change account password")
  void testChangePassword() throws Exception {
    UserAccount user = ControllerModelObjectsFixture.getValidUserAccount();
    String currentPassword = user.getPassword();
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setUsername("change-password");
    userAccountService.saveAndFlush(user);

    restAccountMockMvc
        .perform(
            post("/api/account/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(
                    new PasswordChangeDTO(currentPassword, "new password")))
        )
        .andExpect(status().isOk());

    UserAccount updatedUser = userAccountService.getUserByUsername(user.getUsername())
        .orElse(null);
    assertThat(updatedUser).isNotNull();
    assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isTrue();
  }

  @ParameterizedTest
  @MethodSource("invalidPasswordInputs")
  @WithMockUser("change-invalid-password")
  @DisplayName("fail to change password with invalid password")
  void testChangePasswordWithInvalidPassword(String newPassword) throws Exception {
    UserAccount user = ControllerModelObjectsFixture.getValidUserAccount();
    String currentPassword = user.getPassword();
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userAccountService.saveAndFlush(user);

    restAccountMockMvc
        .perform(
            post("/api/account/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(
                    new PasswordChangeDTO(currentPassword, newPassword)))
        )
        .andExpect(status().isBadRequest());

    UserAccount updatedUser = userAccountService.getUserByUsername(user.getUsername())
        .orElse(null);
    assertThat(updatedUser).isNotNull();
    assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
  }

  @Test
  @DisplayName("reset password")
  void testRequestPasswordReset() throws Exception {
    UserAccount user = ControllerModelObjectsFixture.getValidUserAccount();
    userAccountService.saveAndFlush(user);

    restAccountMockMvc
        .perform(post("/api/account/reset-password/init").content("password-reset@example.com"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("reset password with upper case email")
  void testRequestPasswordResetUpperCaseEmail() throws Exception {
    UserAccount user = ControllerModelObjectsFixture.getValidUserAccount();
    userAccountService.saveAndFlush(user);

    restAccountMockMvc
        .perform(post("/api/account/reset-password/init").content(
            "password-reset-upper-case@EXAMPLE.COM"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("reset password with wrong email")
  void testRequestPasswordResetWrongEmail() throws Exception {
    restAccountMockMvc
        .perform(post("/api/account/reset-password/init").content(
            "password-reset-wrong-email@example.com"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("finish password reset")
  void testFinishPasswordReset() throws Exception {
    UserAccount user = ControllerModelObjectsFixture.getValidUserAccount();
    user.setResetKey(RandomDataUtil.generateResetKey());
    user.setResetDate(Instant.now().minusSeconds(60));
    userAccountService.saveAndFlush(user);

    KeyAndPasswordDTO keyAndPasswordDTO = new KeyAndPasswordDTO();
    keyAndPasswordDTO.setKey(user.getResetKey());
    keyAndPasswordDTO.setNewPassword("new password");

    restAccountMockMvc
        .perform(
            post("/api/account/reset-password/finish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(keyAndPasswordDTO))
        )
        .andExpect(status().isOk());

    UserAccount updatedUser = userAccountService.getUserByUsername(user.getUsername())
        .orElse(null);
    assertThat(updatedUser).isNotNull();
    assertThat(passwordEncoder.matches(keyAndPasswordDTO.getNewPassword(),
        updatedUser.getPassword())).isTrue();
  }

  @ParameterizedTest
  @MethodSource("invalidPasswordInputs")
  @DisplayName("fail to finish password reset with invalid passwords")
  void testFinishPasswordResetWithInvalidPasswords(String password) throws Exception {
    UserAccount user = ControllerModelObjectsFixture.getValidUserAccount();
    user.setResetDate(Instant.now().plusSeconds(60));
    user.setResetKey(RandomDataUtil.generateResetKey());
    userAccountService.saveAndFlush(user);

    KeyAndPasswordDTO keyAndPasswordDTO = new KeyAndPasswordDTO();
    keyAndPasswordDTO.setKey(user.getResetKey());
    keyAndPasswordDTO.setNewPassword(password);

    restAccountMockMvc
        .perform(
            post("/api/account/reset-password/finish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(keyAndPasswordDTO))
        )
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof InvalidPasswordException));

    UserAccount updatedUser = userAccountService.getUserByUsername(user.getUsername())
        .orElse(null);
    assertThat(updatedUser).isNotNull();
    assertThat(passwordEncoder.matches(password == null ? "" : keyAndPasswordDTO.getNewPassword(),
        updatedUser.getPassword())).isFalse();
  }

  @Test
  @DisplayName("fail to finish password reset with expired key")
  void testFinishPasswordWithExpiredDate() throws Exception {
    UserAccount user = ControllerModelObjectsFixture.getValidUserAccount();
    user.setResetDate(Instant.now().minus(2, ChronoUnit.DAYS));
    user.setResetKey(RandomDataUtil.generateResetKey());
    userAccountService.saveAndFlush(user);

    KeyAndPasswordDTO keyAndPasswordDTO = new KeyAndPasswordDTO();
    keyAndPasswordDTO.setKey(user.getResetKey());
    keyAndPasswordDTO.setNewPassword("new password");

    restAccountMockMvc
        .perform(
            post("/api/account/reset-password/finish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(keyAndPasswordDTO))
        )
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ExpiredResetKey));
  }

  @Test
  @DisplayName("fail to finish password reset with nonexistent key")
  void testFinishPasswordResetWrongKey() throws Exception {
    KeyAndPasswordDTO keyAndPasswordDTO = new KeyAndPasswordDTO();
    keyAndPasswordDTO.setKey("wrong reset key");
    keyAndPasswordDTO.setNewPassword("new password");

    restAccountMockMvc
        .perform(
            post("/api/account/reset-password/finish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(keyAndPasswordDTO))
        )
        .andExpect(status().isInternalServerError());
  }

  private static Stream<Arguments> invalidEmailInputs() {
    return Stream.of(
        Arguments.of((Object) null),
        Arguments.of(""),
        Arguments.of("   "),
        Arguments.of("invalid"),
        Arguments.of("invalid"),
        Arguments.of("invalid@"),
        Arguments.of("@invalid")
    );
  }

  private static Stream<Arguments> invalidPasswordInputs() {
    return Stream.of(
        Arguments.of((Object) null),
        Arguments.of(""),
        Arguments.of("   "),
        Arguments.of(RandomDataUtil.generateRandomAlphanumericString(
            ManagedUserDTO.PASSWORD_MIN_LENGTH - 1)),
        Arguments.of(RandomDataUtil.generateRandomAlphanumericString(
            ManagedUserDTO.PASSWORD_MAX_LENGTH + 1))
    );
  }

  private static Stream<Arguments> invalidUsernameInputs() {
    return Stream.of(
        Arguments.of((Object) null),
        Arguments.of(""),
        Arguments.of("   "),
        Arguments.of("funky-log(n"),
        Arguments.of("&%&%&"),
        Arguments.of(RandomDataUtil.generateRandomAlphanumericString(51))
    );
  }
}
