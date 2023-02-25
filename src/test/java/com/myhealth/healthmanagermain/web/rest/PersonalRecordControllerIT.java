package com.myhealth.healthmanagermain.web.rest;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.myhealth.healthmanagermain.IntegrationTest;
import com.myhealth.healthmanagermain.bootstrap.DemoDataBoostrap;
import com.myhealth.healthmanagermain.domain.PersonalRecord;
import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.domain.Workout;
import com.myhealth.healthmanagermain.security.AuthoritiesConstants;
import com.myhealth.healthmanagermain.service.domain.AuthorityService;
import com.myhealth.healthmanagermain.service.domain.PersonalRecordService;
import com.myhealth.healthmanagermain.service.domain.UserAccountService;
import com.myhealth.healthmanagermain.service.domain.WorkoutService;
import com.myhealth.healthmanagermain.util.converter.BigDecimalToString;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.USER)
@IntegrationTest
class PersonalRecordControllerIT {

  @Autowired
  private MockMvc restAccountMockMvc;
  @Autowired
  private AuthorityService authorityService;
  @Autowired
  private UserAccountService userAccountService;
  @Autowired
  private WorkoutService workoutService;
  @Autowired
  private PersonalRecordService personalRecordService;

  private UserAccount user;

  @BeforeEach
  public void initTest() {
    user = ControllerModelObjectsFixture.getValidUserAccount();
  }

  @BeforeEach
  void initAuthorities() {
    authorityService.saveAll(Set.of(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER,
        AuthoritiesConstants.INVALID_ROLE));
  }

  @Test
  @WithMockUser
  @DisplayName("get all personal records for authorized user")
  void getAllPersonalRecordsByUser() throws Exception {
    userAccountService.deleteAll();
    user.setUsername("user");
    userAccountService.saveAndFlush(user);
    Workout workout = workoutService.save(DemoDataBoostrap.getRandomWorkout(user));
    PersonalRecord personalRecord = personalRecordService.save(
        DemoDataBoostrap.getRandomPersonalRecord(user, workout));

    restAccountMockMvc
        .perform(get("/api/personal-record")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.personalRecords").isArray())
        .andExpect(jsonPath("$.personalRecords", hasSize(1)))
        .andExpect(jsonPath("$.personalRecords.[*].exercise").value(
            hasItem(personalRecord.getExercise())))
        .andExpect(jsonPath("$.personalRecords.[*].value").value(
            hasItem(
                new BigDecimalToString().convert(personalRecord.getTarget()))))
        .andExpect(jsonPath("$.personalRecords.[*].recordType").value(
            hasItem(personalRecord.getRecordType().toString())))
        .andExpect(jsonPath("$.personalRecords.[*].workoutNumber").value(
            hasItem(workout.getNumber().intValue())))
        .andExpect(jsonPath("$.personalRecords.[*].date").value(
            hasItem(LocalDate.from(workout.getDate()).toString())));
  }

  @Test
  @WithMockUser(authorities = AuthoritiesConstants.INVALID_ROLE)
  @DisplayName("fail to get personal records for user with invalid role")
  void testGetAllPersonalRecordsByUserWithInvalidRole() throws Exception {
    restAccountMockMvc
        .perform(get("/api/personal-record")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithUnauthenticatedMockUser
  @DisplayName("fail to get personal records for unauthorized user")
  void testNonAuthenticatedUser() throws Exception {
    restAccountMockMvc
        .perform(get("/api/personal-record")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }
}
