package com.myhealth.healthmanagermain.web.rest;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.myhealth.healthmanagermain.IntegrationTest;
import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.domain.enums.UserType;
import com.myhealth.healthmanagermain.repository.UserAccountRepository;
import com.myhealth.healthmanagermain.web.rest.vm.LoginVM;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@IntegrationTest
class UserJWTControllerIT {

  @Autowired
  private UserAccountRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @Transactional
  @DisplayName("get JWT token for existing and valid user")
  void testAuthorize() throws Exception {
    UserAccount user = new UserAccount();
    user.setUsername("user-jwt-controller");
    user.setEmail("user-jwt-controller@example.com");
    user.setActivated(true);
    user.setPassword(passwordEncoder.encode("test"));
    user.setBirthDate(LocalDate.parse("2017-11-15"));
    user.setType(UserType.PRIVATE);

    userRepository.saveAndFlush(user);

    LoginVM login = new LoginVM("user-jwt-controller", "test", false);

    mockMvc
        .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(login)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id_token").isString())
        .andExpect(jsonPath("$.id_token").isNotEmpty())
        .andExpect(header().string("Authorization", not(nullValue())))
        .andExpect(header().string("Authorization", not(is(emptyString()))));
  }

  @Test
  @Transactional
  @DisplayName("get JWT token for existing and valid user with remember me ")
  void testAuthorizeWithRememberMe() throws Exception {
    UserAccount user = new UserAccount();
    user.setUsername("user-jwt-controller-remember-me");
    user.setEmail("user-jwt-controller-remember-me@example.com");
    user.setActivated(true);
    user.setPassword(passwordEncoder.encode("test"));
    user.setBirthDate(LocalDate.parse("2017-11-15"));
    user.setType(UserType.PRIVATE);

    userRepository.saveAndFlush(user);

    LoginVM login = new LoginVM("user-jwt-controller-remember-me", "test", true);

    mockMvc
        .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(login)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id_token").isString())
        .andExpect(jsonPath("$.id_token").isNotEmpty())
        .andExpect(header().string("Authorization", not(nullValue())))
        .andExpect(header().string("Authorization", not(is(emptyString()))));
  }

  @Test
  @DisplayName("get JWT token for user that does not exist")
  void testAuthorizeFails() throws Exception {
    LoginVM login = new LoginVM("wrong-user", "wrong password", false);

    mockMvc
        .perform(post("/api/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(login)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.id_token").doesNotExist())
        .andExpect(header().doesNotExist("Authorization"));
  }
}
