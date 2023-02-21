package com.myhealth.healthmanagermain.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.myhealth.healthmanagermain.IntegrationTest;
import com.myhealth.healthmanagermain.domain.Authority;
import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.domain.enums.UserType;
import com.myhealth.healthmanagermain.repository.UserAccountRepository;
import com.myhealth.healthmanagermain.security.AuthoritiesConstants;
import com.myhealth.healthmanagermain.service.domain.AuthorityService;
import com.myhealth.healthmanagermain.service.domain.UserAccountService;
import com.myhealth.healthmanagermain.service.dto.AdminUserDTO;
import com.myhealth.healthmanagermain.service.mapper.UserMapper;
import com.myhealth.healthmanagermain.web.rest.dto.ManagedUserDTO;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import lombok.NonNull;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
@IntegrationTest
class UserControllerIT {

  private static final String DEFAULT_USERNAME = "username1";
  private static final String UPDATED_USERNAME = "username2";

  private static final Long DEFAULT_ID = 1L;

  private static final String DEFAULT_PASSWORD = "password1";
  private static final String UPDATED_PASSWORD = "password2";

  private static final String DEFAULT_EMAIL = "dummy1@localhost";
  private static final String UPDATED_EMAIL = "dummy2@localhost";

  private static final String DEFAULT_FIRSTNAME = "first1";
  private static final String UPDATED_FIRSTNAME = "first1";

  private static final String DEFAULT_LASTNAME = "last1";
  private static final String UPDATED_LASTNAME = "last2";

  private static final String DEFAULT_IMAGEURL = "http://placehold.it/50x50";
  private static final String UPDATED_IMAGEURL = "http://placehold.it/40x40";

  private static final String DEFAULT_LANGKEY = "en";
  private static final String UPDATED_LANGKEY = "fr";

  private static final UserType DEFAULT_USER_TYPE = UserType.PRIVATE;
  private static final UserType UPDATED_USER_TYPE = UserType.PERSONAL_TRAINER;

  private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.parse("2000-01-01");
  private static final LocalDate UPDATED_BIRTH_DATE = LocalDate.parse("2000-01-02");

  @Autowired
  private UserAccountService userAccountService;

  @Autowired
  private AuthorityService authorityService;

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private MockMvc restUserMockMvc;

  private UserAccount user;

  @BeforeEach
  public void setup() {
    cacheManager.getCache(UserAccountRepository.USERS_BY_USERNAME_CACHE).clear();
    cacheManager.getCache(UserAccountRepository.USERS_BY_EMAIL_CACHE).clear();
  }

  @BeforeEach
  void initAuthorities() {
    authorityService.saveAll(Set.of(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER));
  }

  public static UserAccount createEntity() {
    UserAccount user = new UserAccount();
    user.setUsername(DEFAULT_USERNAME);
    user.setPassword(RandomStringUtils.randomAlphanumeric(60));
    user.setActivated(true);
    user.setEmail(DEFAULT_EMAIL);
    user.setFirstName(DEFAULT_FIRSTNAME);
    user.setLastName(DEFAULT_LASTNAME);
    user.setImageUrl(DEFAULT_IMAGEURL);
    user.setLangKey(DEFAULT_LANGKEY);
    user.setType(DEFAULT_USER_TYPE);
    user.setBirthDate(DEFAULT_BIRTH_DATE);
    return user;
  }

  public static UserAccount initTestUser(UserAccountService userAccountService) {
    userAccountService.deleteAll();
    return createEntity();
  }

  @BeforeEach
  public void initTest() {
    user = initTestUser(userAccountService);
  }

  @Test
  @DisplayName("create new user")
  void createUser() throws Exception {
    int databaseSizeBeforeCreate = userAccountService.getAll().size();

    ManagedUserDTO managedUserDTO = getManagedUserVMWithDefaultFields();

    restUserMockMvc
        .perform(
            post("/api/admin/users").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(managedUserDTO))
        )
        .andExpect(status().isCreated());

    assertPersistedUsers(users -> {
      assertThat(users).hasSize(databaseSizeBeforeCreate + 1);
      UserAccount testUser = users.get(users.size() - 1);
      assertUserAccountHasDefaultFields(testUser);
    });
  }

  @Test
  @DisplayName("fail to create new user with already existing id")
  void createUserWithExistingId() throws Exception {
    int databaseSizeBeforeCreate = userAccountService.getAll().size();

    ManagedUserDTO managedUserDTO = getManagedUserVMWithDefaultFields();
    managedUserDTO.setId(DEFAULT_ID);

    restUserMockMvc
        .perform(
            post("/api/admin/users").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(managedUserDTO))
        )
        .andExpect(status().isBadRequest());

    assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
  }

  @Test
  @DisplayName("fail to create new user with already existing username")
  void createUserWithExistingLogin() throws Exception {
    userAccountService.saveAndFlush(user);
    int databaseSizeBeforeCreate = userAccountService.getAll().size();

    ManagedUserDTO managedUserDTO = getManagedUserVMWithDefaultFields();

    restUserMockMvc
        .perform(
            post("/api/admin/users").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(managedUserDTO))
        )
        .andExpect(status().isBadRequest());

    assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
  }

  @Test
  @DisplayName("fail to create user for already existing email")
  void createUserWithExistingEmail() throws Exception {
    userAccountService.saveAndFlush(user);
    int databaseSizeBeforeCreate = userAccountService.getAll().size();

    ManagedUserDTO managedUserDTO = getManagedUserVMWithDefaultFields();
    managedUserDTO.setUsername("anotherlogin");

    restUserMockMvc
        .perform(
            post("/api/admin/users").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(managedUserDTO))
        )
        .andExpect(status().isBadRequest());

    assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
  }

  @Test
  @DisplayName("get all users in database")
  void getAllUsers() throws Exception {
    userAccountService.saveAndFlush(user);

    restUserMockMvc
        .perform(get("/api/admin/users?sort=id,desc").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
        .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRSTNAME)))
        .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LASTNAME)))
        .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
        .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGEURL)))
        .andExpect(jsonPath("$.[*].langKey").value(hasItem(DEFAULT_LANGKEY)))
        .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_USER_TYPE.toString())))
        .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())));
  }

  @Test
  @DisplayName("get user by username")
  void getUser() throws Exception {
    userAccountService.saveAndFlush(user);

    assertUserDoesExistInCache();

    restUserMockMvc
        .perform(get("/api/admin/users/{username}", user.getUsername()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.username").value(user.getUsername()))
        .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRSTNAME))
        .andExpect(jsonPath("$.lastName").value(DEFAULT_LASTNAME))
        .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
        .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGEURL))
        .andExpect(jsonPath("$.langKey").value(DEFAULT_LANGKEY))
        .andExpect(jsonPath("$.type").value(DEFAULT_USER_TYPE.toString()))
        .andExpect(jsonPath("$.birthDate").value(DEFAULT_BIRTH_DATE.toString()));

    assertUserExistsInCache();
  }

  private void assertUserDoesExistInCache() {
    assertThat(
        cacheManager.getCache(UserAccountRepository.USERS_BY_USERNAME_CACHE)
            .get(user.getUsername())).isNull();
  }

  private void assertUserExistsInCache() {
    assertThat(cacheManager.getCache(UserAccountRepository.USERS_BY_USERNAME_CACHE)
        .get(user.getUsername())).isNotNull();
  }

  @Test
  @DisplayName("fail to get user by username that does not exist")
  void getNonExistingUser() throws Exception {
    restUserMockMvc.perform(get("/api/admin/users/unknown")).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("update user")
  void updateUser() throws Exception {
    userAccountService.saveAndFlush(user);
    int databaseSizeBeforeUpdate = userAccountService.getAll().size();

    UserAccount updatedUser = userAccountService.getUserById(user.getId()).get();

    ManagedUserDTO managedUserDTO = getManagedUserWithUpdatedFields();
    managedUserDTO.setId(updatedUser.getId());
    managedUserDTO.setUsername(updatedUser.getUsername());
    managedUserDTO.setActivated(updatedUser.isActivated());

    restUserMockMvc
        .perform(
            put("/api/admin/users").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(managedUserDTO))
        )
        .andExpect(status().isOk());

    assertPersistedUsers(users -> {
      assertThat(users).hasSize(databaseSizeBeforeUpdate);
      UserAccount testUser = users.stream().filter(usr -> usr.getId().equals(updatedUser.getId()))
          .findFirst().get();
      assertThat(testUser.getFirstName()).isEqualTo(UPDATED_FIRSTNAME);
      assertThat(testUser.getLastName()).isEqualTo(UPDATED_LASTNAME);
      assertThat(testUser.getEmail()).isEqualTo(UPDATED_EMAIL);
      assertThat(testUser.getImageUrl()).isEqualTo(UPDATED_IMAGEURL);
      assertThat(testUser.getLangKey()).isEqualTo(UPDATED_LANGKEY);
      assertThat(testUser.getType()).isEqualTo(UPDATED_USER_TYPE);
      assertThat(testUser.getBirthDate()).isEqualTo(UPDATED_BIRTH_DATE);
      assertThat(testUser.getCreatedDate()).isEqualTo(updatedUser.getCreatedDate());
    });
  }

  @Test
  @DisplayName("update user username field")
  void updateUserLogin() throws Exception {
    userAccountService.saveAndFlush(user);
    int databaseSizeBeforeUpdate = userAccountService.getAll().size();

    UserAccount updatedUser = userAccountService.getUserById(user.getId()).get();

    ManagedUserDTO managedUserDTO = getManagedUserWithUpdatedFields();
    managedUserDTO.setId(updatedUser.getId());
    managedUserDTO.setActivated(updatedUser.isActivated());

    restUserMockMvc
        .perform(
            put("/api/admin/users").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(managedUserDTO))
        )
        .andExpect(status().isOk());

    assertPersistedUsers(users -> {
      assertThat(users).hasSize(databaseSizeBeforeUpdate);
      UserAccount testUser = users.stream().filter(usr -> usr.getId().equals(updatedUser.getId()))
          .findFirst().get();
      assertThat(testUser.getUsername()).isEqualTo(UPDATED_USERNAME);
      assertThat(testUser.getFirstName()).isEqualTo(UPDATED_FIRSTNAME);
      assertThat(testUser.getLastName()).isEqualTo(UPDATED_LASTNAME);
      assertThat(testUser.getEmail()).isEqualTo(UPDATED_EMAIL);
      assertThat(testUser.getImageUrl()).isEqualTo(UPDATED_IMAGEURL);
      assertThat(testUser.getLangKey()).isEqualTo(UPDATED_LANGKEY);
      assertThat(testUser.getType()).isEqualTo(UPDATED_USER_TYPE);
      assertThat(testUser.getBirthDate()).isEqualTo(UPDATED_BIRTH_DATE);
      assertThat(testUser.getCreatedDate()).isEqualTo(updatedUser.getCreatedDate());
    });
  }

  @Test
  @DisplayName("fail user update for already existing email")
  void updateUserExistingEmail() throws Exception {
    userAccountService.saveAndFlush(user);

    UserAccount anotherUser = ControllerModelObjectsFixture.getValidUserAccount();
    userAccountService.saveAndFlush(anotherUser);

    UserAccount updatedUser = userAccountService.getUserById(user.getId()).get();

    ManagedUserDTO managedUserDTO = ManagedUserDTO.fromUserAccount(updatedUser);
    managedUserDTO.setEmail(anotherUser.getEmail());

    restUserMockMvc
        .perform(
            put("/api/admin/users").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(managedUserDTO))
        )
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("fail user update for already existing username")
  void updateUserExistingLogin() throws Exception {
    userAccountService.saveAndFlush(user);

    UserAccount anotherUser = ControllerModelObjectsFixture.getValidUserAccount();
    anotherUser.setUsername("dummy");
    userAccountService.saveAndFlush(anotherUser);

    UserAccount updatedUser = userAccountService.getUserById(user.getId()).get();

    ManagedUserDTO managedUserDTO = ManagedUserDTO.fromUserAccount(updatedUser);
    managedUserDTO.setUsername("dummy");
    managedUserDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

    restUserMockMvc
        .perform(
            put("/api/admin/users").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(managedUserDTO))
        )
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("delete user")
  void deleteUser() throws Exception {
    userAccountService.saveAndFlush(user);
    int databaseSizeBeforeDelete = userAccountService.getAll().size();

    restUserMockMvc
        .perform(
            get("/api/admin/users/{username}", user.getUsername()).accept(
                MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    assertUserExistsInCache();

    restUserMockMvc
        .perform(
            delete("/api/admin/users/{username}", user.getUsername()).accept(
                MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    assertUserDoesExistInCache();

    assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeDelete - 1));
  }

  @Test
  void testUserEquals() throws Exception {
    TestUtil.equalsVerifier(UserAccount.class);
    UserAccount user1 = new UserAccount();
    user1.setId(DEFAULT_ID);
    UserAccount user2 = new UserAccount();
    user2.setId(user1.getId());
    assertThat(user1).isEqualTo(user2);
    user2.setId(2L);
    assertThat(user1).isNotEqualTo(user2);
    user1.setId(null);
    assertThat(user1).isNotEqualTo(user2);
  }

  @Test
  @DisplayName("map admin user dto to user account")
  void testUserDTOtoUser() {
    AdminUserDTO userDTO = new AdminUserDTO();
    userDTO.setId(DEFAULT_ID);
    userDTO.setUsername(DEFAULT_USERNAME);
    userDTO.setFirstName(DEFAULT_FIRSTNAME);
    userDTO.setLastName(DEFAULT_LASTNAME);
    userDTO.setEmail(DEFAULT_EMAIL);
    userDTO.setActivated(true);
    userDTO.setImageUrl(DEFAULT_IMAGEURL);
    userDTO.setLangKey(DEFAULT_LANGKEY);
    userDTO.setType(DEFAULT_USER_TYPE);
    userDTO.setBirthDate(DEFAULT_BIRTH_DATE);
    userDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

    UserAccount user = UserMapper.userAdminDTOToUserAccount(userDTO);
    assertThat(user.getId()).isEqualTo(DEFAULT_ID);
    assertThat(user.getUsername()).isEqualTo(DEFAULT_USERNAME);
    assertThat(user.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
    assertThat(user.getLastName()).isEqualTo(DEFAULT_LASTNAME);
    assertThat(user.getEmail()).isEqualTo(DEFAULT_EMAIL);
    assertThat(user.isActivated()).isTrue();
    assertThat(user.getImageUrl()).isEqualTo(DEFAULT_IMAGEURL);
    assertThat(user.getLangKey()).isEqualTo(DEFAULT_LANGKEY);
    assertThat(user.getType()).isEqualTo(DEFAULT_USER_TYPE);
    assertThat(user.getBirthDate()).isEqualTo(DEFAULT_BIRTH_DATE);
    assertThat(user.getCreatedDate()).isNotNull();
    assertThat(user.getAuthorities()).extracting("name").containsExactly(AuthoritiesConstants.USER);
  }

  @Test
  @DisplayName("map user account to admin user account")
  void testUserToUserDTO() {
    user.setId(DEFAULT_ID);
    user.setCreatedDate(Instant.now());
    Set<Authority> authorities = new HashSet<>();
    Authority authority = new Authority();
    authority.setName(AuthoritiesConstants.USER);
    authorities.add(authority);
    user.setAuthorities(authorities);

    AdminUserDTO userDTO = UserMapper.userAccountToAdminUserDTO(user);

    assertThat(userDTO.getId()).isEqualTo(DEFAULT_ID);
    assertThat(userDTO.getUsername()).isEqualTo(DEFAULT_USERNAME);
    assertThat(userDTO.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
    assertThat(userDTO.getLastName()).isEqualTo(DEFAULT_LASTNAME);
    assertThat(userDTO.getEmail()).isEqualTo(DEFAULT_EMAIL);
    assertThat(userDTO.isActivated()).isTrue();
    assertThat(userDTO.getImageUrl()).isEqualTo(DEFAULT_IMAGEURL);
    assertThat(userDTO.getLangKey()).isEqualTo(DEFAULT_LANGKEY);
    assertThat(userDTO.getType()).isEqualTo(user.getType());
    assertThat(userDTO.getBirthDate()).isEqualTo(user.getBirthDate());
    assertThat(userDTO.getAuthorities()).containsExactly(AuthoritiesConstants.USER);
    assertThat(userDTO.toString()).isNotNull();
  }

  @Test
  void testAuthorityEquals() {
    Authority authorityA = new Authority();
    assertThat(authorityA).isNotEqualTo(null).isNotEqualTo(new Object());
    assertThat(authorityA.hashCode()).isZero();
    assertThat(authorityA.toString()).isNotNull();

    Authority authorityB = new Authority();
    assertThat(authorityA).isEqualTo(authorityB);

    authorityB.setName(AuthoritiesConstants.ADMIN);
    assertThat(authorityA).isNotEqualTo(authorityB);

    authorityA.setName(AuthoritiesConstants.USER);
    assertThat(authorityA).isNotEqualTo(authorityB);

    authorityB.setName(AuthoritiesConstants.USER);
    assertThat(authorityA).isEqualTo(authorityB).hasSameHashCodeAs(authorityB);
  }

  private void assertPersistedUsers(Consumer<List<UserAccount>> userAssertion) {
    userAssertion.accept(userAccountService.getAll());
  }

  @NonNull
  private static ManagedUserDTO getManagedUserVMWithDefaultFields() {
    ManagedUserDTO managedUserDTO = new ManagedUserDTO();
    managedUserDTO.setUsername(DEFAULT_USERNAME);
    managedUserDTO.setPassword(DEFAULT_PASSWORD);
    managedUserDTO.setFirstName(DEFAULT_FIRSTNAME);
    managedUserDTO.setLastName(DEFAULT_LASTNAME);
    managedUserDTO.setEmail(DEFAULT_EMAIL);
    managedUserDTO.setActivated(true);
    managedUserDTO.setImageUrl(DEFAULT_IMAGEURL);
    managedUserDTO.setLangKey(DEFAULT_LANGKEY);
    managedUserDTO.setType(DEFAULT_USER_TYPE);
    managedUserDTO.setBirthDate(DEFAULT_BIRTH_DATE);
    managedUserDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));
    return managedUserDTO;
  }

  private static void assertUserAccountHasDefaultFields(UserAccount testUser) {
    assertThat(testUser.getId()).isPositive();
    assertThat(testUser.getUsername()).isEqualTo(DEFAULT_USERNAME);
    assertThat(testUser.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
    assertThat(testUser.getLastName()).isEqualTo(DEFAULT_LASTNAME);
    assertThat(testUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
    assertThat(testUser.getImageUrl()).isEqualTo(DEFAULT_IMAGEURL);
    assertThat(testUser.getLangKey()).isEqualTo(DEFAULT_LANGKEY);
    assertThat(testUser.getType()).isEqualTo(DEFAULT_USER_TYPE);
    assertThat(testUser.getBirthDate()).isEqualTo(DEFAULT_BIRTH_DATE);
  }

  private static ManagedUserDTO getManagedUserWithUpdatedFields() {
    ManagedUserDTO managedUserDTO = new ManagedUserDTO();
    managedUserDTO.setUsername(UPDATED_USERNAME);
    managedUserDTO.setPassword(UPDATED_PASSWORD);
    managedUserDTO.setFirstName(UPDATED_FIRSTNAME);
    managedUserDTO.setLastName(UPDATED_LASTNAME);
    managedUserDTO.setEmail(UPDATED_EMAIL);
    managedUserDTO.setImageUrl(UPDATED_IMAGEURL);
    managedUserDTO.setLangKey(UPDATED_LANGKEY);
    managedUserDTO.setType(UPDATED_USER_TYPE);
    managedUserDTO.setBirthDate(UPDATED_BIRTH_DATE);
    managedUserDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));
    return managedUserDTO;
  }
}