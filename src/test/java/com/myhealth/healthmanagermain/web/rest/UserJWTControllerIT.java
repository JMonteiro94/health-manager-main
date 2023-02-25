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
import com.myhealth.healthmanagermain.service.domain.UserAccountService;
import com.myhealth.healthmanagermain.web.rest.dto.LoginDTO;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@IntegrationTest
class UserJWTControllerIT {

  private static final String API_AUTHENTICATE_PATH = "/api/authenticate";
  @Autowired
  private UserAccountService userAccountService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("get JWT token for existing and valid user")
  void testAuthorize() throws Exception {
    UserAccount user = new UserAccount();
    user.setUsername("user-jwt-controller");
    user.setEmail("user-jwt-controller@example.com");
    user.setActivated(true);
    user.setPassword(passwordEncoder.encode("test"));
    user.setBirthDate(LocalDate.parse("2017-11-15"));
    user.setType(UserType.PRIVATE);

    userAccountService.saveAndFlush(user);

    LoginDTO loginDTO = new LoginDTO("user-jwt-controller", "test", false);

    mockMvc
        .perform(post(API_AUTHENTICATE_PATH).contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(loginDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id_token").isString())
        .andExpect(jsonPath("$.id_token").isNotEmpty())
        .andExpect(header().string("Authorization", not(nullValue())))
        .andExpect(header().string("Authorization", not(is(emptyString()))));
  }

  @Test
  @DisplayName("get JWT token for existing and valid user with remember me ")
  void testAuthorizeWithRememberMe() throws Exception {
    UserAccount user = new UserAccount();
    user.setUsername("user-jwt-controller-remember-me");
    user.setEmail("user-jwt-controller-remember-me@example.com");
    user.setActivated(true);
    user.setPassword(passwordEncoder.encode("test"));
    user.setBirthDate(LocalDate.parse("2017-11-15"));
    user.setType(UserType.PRIVATE);

    userAccountService.saveAndFlush(user);

    LoginDTO loginDTO = new LoginDTO("user-jwt-controller-remember-me", "test", true);

    mockMvc
        .perform(post(API_AUTHENTICATE_PATH).contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(loginDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id_token").isString())
        .andExpect(jsonPath("$.id_token").isNotEmpty())
        .andExpect(header().string("Authorization", not(nullValue())))
        .andExpect(header().string("Authorization", not(is(emptyString()))));
  }

  @Test
  @DisplayName("get JWT token for user that does not exist")
  void testAuthorizeFails() throws Exception {
    LoginDTO loginDTO = new LoginDTO("wrong-user", "wrong password", false);

    mockMvc
        .perform(post(API_AUTHENTICATE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(loginDTO)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.id_token").doesNotExist())
        .andExpect(header().doesNotExist("Authorization"));
  }
}
