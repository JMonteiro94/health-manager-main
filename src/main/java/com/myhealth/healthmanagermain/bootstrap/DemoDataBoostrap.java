package com.myhealth.healthmanagermain.bootstrap;

import com.myhealth.healthmanagermain.aop.timer.MeasureTime;
import com.myhealth.healthmanagermain.config.Constants;
import com.myhealth.healthmanagermain.domain.Authority;
import com.myhealth.healthmanagermain.domain.Goal;
import com.myhealth.healthmanagermain.domain.PersonalRecord;
import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.domain.UserPreferences;
import com.myhealth.healthmanagermain.domain.Workout;
import com.myhealth.healthmanagermain.domain.enums.Currency;
import com.myhealth.healthmanagermain.domain.enums.GoalType;
import com.myhealth.healthmanagermain.domain.enums.Language;
import com.myhealth.healthmanagermain.domain.enums.TimeWindow;
import com.myhealth.healthmanagermain.domain.enums.UserType;
import com.myhealth.healthmanagermain.domain.enums.WeightSystem;
import com.myhealth.healthmanagermain.domain.enums.WorkoutType;
import com.myhealth.healthmanagermain.security.AuthoritiesConstants;
import com.myhealth.healthmanagermain.service.domain.AuthorityService;
import com.myhealth.healthmanagermain.service.domain.GoalService;
import com.myhealth.healthmanagermain.service.domain.PersonalRecordService;
import com.myhealth.healthmanagermain.service.domain.UserAccountService;
import com.myhealth.healthmanagermain.service.domain.WorkoutService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("demo")
@Component
@AllArgsConstructor
public class DemoDataBoostrap implements ApplicationRunner {

  @NonNull
  private final AuthorityService authorityService;
  @NonNull
  private final UserAccountService userAccountService;
  @NonNull
  private final PasswordEncoder passwordEncoder;
  @NonNull
  private final PersonalRecordService personalRecordService;
  @NonNull
  private final WorkoutService workoutService;
  @NonNull
  private final GoalService goalService;

  @Override
  @MeasureTime
  public void run(ApplicationArguments args) {
    loadAuthorities();
    loadUserAccounts();
  }

  public void loadAuthorities() {
    Set<String> authorities = Set.of(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER,
        AuthoritiesConstants.PT, AuthoritiesConstants.ANONYMOUS);
    authorityService.saveAll(authorities);
  }

  public void loadUserAccounts() {
    userAccountService.saveAll(List.of(getValidUserAccount()));
    for (int i = 0; i < 5; i++) {
      UserAccount randomUserAccount = getValidRandomUserAccount();
      userAccountService.save(randomUserAccount);
      Workout randomWorkout = workoutService.save(getRandomWorkout(randomUserAccount));
      personalRecordService.save(getRandomPersonalRecord(randomUserAccount, randomWorkout));
      goalService.save(getRandomGoal(randomUserAccount));
    }
  }

  public UserAccount getValidRandomUserAccount() {
    return UserAccount.builder()
        .username(RandomDataUtil.generateRandomUsername())
        .password(RandomDataUtil.generateEncodePassword())
        .activated(true)
        .email(RandomDataUtil.generateRandomEmailString())
        .firstName(RandomDataUtil.generateRandomName())
        .lastName(RandomDataUtil.generateRandomName())
        .imageUrl("http://placehold.it/50x50")
        .resetKey(RandomDataUtil.generateResetKey())
        .langKey(Constants.DEFAULT_LANGUAGE)
        .type(UserType.PRIVATE)
        .resetDate(Instant.now().plusSeconds(60))
        .birthDate(RandomDataUtil.generateRandomBirthDate())
        .authorities(Set.of(new Authority(AuthoritiesConstants.USER)))
        .preferences(getUserPreferences()).build();
  }

  public UserAccount getValidUserAccount() {
    return UserAccount.builder()
        .username("user")
        .password(passwordEncoder.encode("user"))
        .activated(true)
        .email("test@gmail.com")
        .firstName("jonh")
        .lastName("doe")
        .imageUrl("http://placehold.it/50x50")
        .langKey(Constants.DEFAULT_LANGUAGE)
        .type(UserType.PRIVATE)
        .birthDate(LocalDate.parse("2000-01-01"))
        .authorities(Set.of(new Authority(AuthoritiesConstants.ADMIN)))
        .preferences(getUserPreferences()).build();
  }

  private UserPreferences getUserPreferences() {
    return UserPreferences.builder()
        .country("PT")
        .currency(Currency.EURO)
        .language(Language.ENGLISH)
        .weightSystem(WeightSystem.METRIC).build();
  }

  public static PersonalRecord getRandomPersonalRecord(UserAccount userAccount, Workout workout) {
    return PersonalRecord.builder()
        .exercise("deadlift")
        .target(RandomDataUtil.generateRandomPersonalRecordTarget())
        .weightSystem(WeightSystem.METRIC)
        .recordType(RandomDataUtil.generateRandomRecordType())
        .user(userAccount)
        .workout(workout)
        .build();
  }

  public static Workout getRandomWorkout(UserAccount userAccount) {
    return Workout.builder()
        .place(RandomDataUtil.generateRandomName())
        .number((long) RandomDataUtil.generateRandomNonZeroInteger())
        .date(ZonedDateTime.now())
        .type(WorkoutType.Anaerobic)
        .startingWeight(RandomDataUtil.generateRandomWeight())
        .weekDay(RandomDataUtil.generateRandomDayOfWeek())
        .finishWeight(RandomDataUtil.generateRandomWeight())
        .user(userAccount)
        .build();
  }

  public static Goal getRandomGoal(UserAccount userAccount) {
    return Goal.builder()
        .name("deadlift PR")
        .goalType(GoalType.PR)
        .windowTime(TimeWindow.QUARTER)
        .windowNumber(1)
        .targetValue(new BigDecimal(1))
        .startDate(RandomDataUtil.generateRandomDate())
        .finishDate(RandomDataUtil.generateRandomDate())
        .user(userAccount)
        .build();
  }
}
