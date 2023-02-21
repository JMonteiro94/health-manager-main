package com.myhealth.healthmanagermain.web.rest;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import lombok.experimental.UtilityClass;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.TypeSafeMatcher;


@UtilityClass
public final class TestUtil {

  private static final ObjectMapper mapper;

  static {
    mapper = createObjectMapper();
  }

  private static ObjectMapper createObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    mapper.registerModule(new JavaTimeModule());
    return mapper;
  }

  public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
    return mapper.writeValueAsBytes(object);
  }

  public static byte[] createByteArray(int size, String data) {
    byte[] byteArray = new byte[size];
    for (int i = 0; i < size; i++) {
      byteArray[i] = Byte.parseByte(data, 2);
    }
    return byteArray;
  }

  public static class ZonedDateTimeMatcher extends TypeSafeDiagnosingMatcher<String> {

    private final ZonedDateTime date;

    public ZonedDateTimeMatcher(ZonedDateTime date) {
      this.date = date;
    }

    @Override
    protected boolean matchesSafely(String item, Description mismatchDescription) {
      try {
        if (!date.isEqual(ZonedDateTime.parse(item))) {
          mismatchDescription.appendText("was ").appendValue(item);
          return false;
        }
        return true;
      } catch (DateTimeParseException e) {
        mismatchDescription.appendText("was ").appendValue(item)
            .appendText(", which could not be parsed as a ZonedDateTime");
        return false;
      }
    }

    @Override
    public void describeTo(Description description) {
      description.appendText("a String representing the same Instant as ").appendValue(date);
    }
  }

  public static ZonedDateTimeMatcher sameInstant(ZonedDateTime date) {
    return new ZonedDateTimeMatcher(date);
  }

  public static class NumberMatcher extends TypeSafeMatcher<Number> {

    final BigDecimal value;

    public NumberMatcher(BigDecimal value) {
      this.value = value;
    }

    @Override
    public void describeTo(Description description) {
      description.appendText("a numeric value is ").appendValue(value);
    }

    @Override
    protected boolean matchesSafely(Number item) {
      BigDecimal bigDecimal = asDecimal(item);
      return bigDecimal != null && value.compareTo(bigDecimal) == 0;
    }

    private static BigDecimal asDecimal(Number item) {
      if (item == null) {
        return null;
      }
      if (item instanceof BigDecimal) {
        return (BigDecimal) item;
      } else if (item instanceof Long) {
        return BigDecimal.valueOf((Long) item);
      } else if (item instanceof Integer) {
        return BigDecimal.valueOf((Integer) item);
      } else if (item instanceof Double) {
        return BigDecimal.valueOf((Double) item);
      } else if (item instanceof Float) {
        return BigDecimal.valueOf((Float) item);
      } else {
        return BigDecimal.valueOf(item.doubleValue());
      }
    }
  }

  public static NumberMatcher sameNumber(BigDecimal number) {
    return new NumberMatcher(number);
  }

  public static <T> void equalsVerifier(Class<T> clazz) throws Exception {
    T domainObject1 = clazz.getConstructor().newInstance();
    assertThat(domainObject1.toString()).isNotNull();
    assertThat(domainObject1).isEqualTo(domainObject1);
    assertThat(domainObject1).hasSameHashCodeAs(domainObject1);
    // Test with an instance of another class
    Object testOtherObject = new Object();
    assertThat(domainObject1).isNotEqualTo(testOtherObject);
    assertThat(domainObject1).isNotEqualTo(null);
    // Test with an instance of the same class
    T domainObject2 = clazz.getConstructor().newInstance();
    assertThat(domainObject1).isNotEqualTo(domainObject2);
    // HashCodes are equals because the objects are not persisted yet
    assertThat(domainObject1).hasSameHashCodeAs(domainObject2);
  }
}