package com.myhealth.healthmanagermain.web.rest;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.myhealth.healthmanagermain.IntegrationTest;
import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.repository.UserAccountRepository;
import com.myhealth.healthmanagermain.security.AuthoritiesConstants;
import com.myhealth.healthmanagermain.service.domain.AuthorityService;
import com.myhealth.healthmanagermain.service.domain.UserAccountService;
import java.util.Set;
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
class PublicUserControllerIT {

  private static final String DEFAULT_LOGIN = "username1";
  private static final String DEFAULT_LANGKEY = "en";
  private static final String DEFAULT_EMAIL = "dummy1@localhost";

  @Autowired
  private AuthorityService authorityService;

  @Autowired
  private UserAccountService userAccountService;

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
  public void initTest() {
    user = UserControllerIT.initTestUser(userAccountService);
  }

  @Test
  @DisplayName("get all users public information")
  void getAllPublicUsers() throws Exception {
    userAccountService.saveAndFlush(user);

    restUserMockMvc
        .perform(get("/api/users?sort=username,desc").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_LOGIN)))
        .andExpect(jsonPath("$.[*].email").value(hasItems(DEFAULT_EMAIL)))
        .andExpect(jsonPath("$.[*].langKey").value(hasItems(DEFAULT_LANGKEY)))
        .andExpect(jsonPath("$.[*].firstName").doesNotExist())
        .andExpect(jsonPath("$.[*].lastName").doesNotExist())
        .andExpect(jsonPath("$.[*].password").doesNotExist())
        .andExpect(jsonPath("$.[*].imageUrl").doesNotExist());
  }

  @Test
  @DisplayName("get all authorities")
  void getAllAuthorities() throws Exception {

    authorityService.saveAll(Set.of(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER));

    restUserMockMvc
        .perform(get("/api/authorities").accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isArray())
        .andExpect(
            jsonPath("$").value(hasItems(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)));
  }

  @Test
  @DisplayName("get all users public information sorted by allowed fields")
  void getAllUsersSortedByParameters() throws Exception {
    userAccountService.saveAndFlush(user);

    // bad requests
    restUserMockMvc.perform(get("/api/users?sort=resetKey,desc").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    restUserMockMvc.perform(get("/api/users?sort=password,desc").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    restUserMockMvc.perform(
            get("/api/users?sort=firstName,desc").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    restUserMockMvc.perform(get("/api/users?sort=lastName,desc").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    restUserMockMvc
        .perform(
            get("/api/users?sort=resetKey,desc&sort=id,desc").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    // good requests
    restUserMockMvc.perform(
            get("/api/users?sort=username,desc").accept(
                MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    restUserMockMvc.perform(
            get("/api/users?sort=username,desc&sort=langKey,desc&sort=email,desc").accept(
                MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}
