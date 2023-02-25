package com.myhealth.healthmanagermain.bootstrap;

import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import com.myhealth.healthmanagermain.domain.enums.RecordType;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Random;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

@UtilityClass
public final class RandomDataUtil {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  private static final Random random = new Random();

  private static final FakeValuesService fakeValuesService = new FakeValuesService(
      new Locale("en-GB"), new RandomService());

  static {
    SECURE_RANDOM.nextBytes(new byte[64]);
  }

  public static String generateRandomUsername() {
    return fakeValuesService.regexify("[a-zA-Z1-9]{10}");
  }

  public static String generateRandomName() {
    return fakeValuesService.regexify("[a-zA-Z]{10}");
  }

  public static String generateRandomEmailString() {
    return fakeValuesService.bothify("????##@gmail.com");
  }

  public static String generateRandomAlphanumericString() {
    return RandomStringUtils.random(20, 0, 0, true, true, null, SECURE_RANDOM);
  }

  public static String generateRandomAlphanumericString(int length) {
    return RandomStringUtils.random(length, 0, 0, true, true, null, SECURE_RANDOM);
  }

  public static String generatePassword() {
    return generateRandomAlphanumericString();
  }

  public static String generateEncodePassword() {
    return RandomStringUtils.randomAlphanumeric(60);
  }

  public static String generateActivationKey() {
    return generateRandomAlphanumericString();
  }

  public static String generateResetKey() {
    return generateRandomAlphanumericString();
  }

  public static LocalDate generateRandomBirthDate() {
    int year = generateRandomYear();
    int month = generateRandomMonth();
    int days = generateRandomDayInMonth();
    StringBuilder sb = new StringBuilder();
    sb.append(year).append("-");
    if (month < 10) {
      sb.append("0");
    }
    sb.append(month).append("-");
    if (days < 10) {
      sb.append("0");
    }
    sb.append(days);

    return LocalDate.parse(sb.toString());
  }

  public static int generateRandomYear() {
    return 1950 + generateRandomInt(0, 2022 - 1950);
  }

  public static int generateRandomMonth() {
    return generateRandomInt(1, 12);
  }

  public static int generateRandomDayInMonth() {
    return generateRandomInt(1, 28);
  }

  public static BigDecimal generateRandomPersonalRecordTarget() {
    return BigDecimal.valueOf(generateRandomInt(1, 500));
  }

  public static int generateRandomInt(int origin, int bound) {
    if (bound <= origin) {
      throw new IllegalArgumentException("upper limit can not be less or equal to origin");
    }
    return random.nextInt(origin, bound + 1);
  }

  public static int generateRandomNonZeroInteger() {
    return generateRandomInt(1, Integer.MAX_VALUE - 1);
  }

  public static BigDecimal generateRandomWeight() {
    return BigDecimal.valueOf(generateRandomInt(1, 150));
  }

  public static DayOfWeek generateRandomDayOfWeek() {
    int rand = generateRandomInt(1, 7);
    return DayOfWeek.of(rand);
  }

  public static RecordType generateRandomRecordType() {
    int rand = generateRandomInt(1, 6);
    return RecordType.of(rand);
  }
}
