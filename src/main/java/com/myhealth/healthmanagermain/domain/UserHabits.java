package com.myhealth.healthmanagermain.domain;

import java.math.BigDecimal;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class UserHabits {

  private Boolean isSmoker;
  private Integer cigarettesPerDay;
  private Integer smokingYears;
  private Integer averageWorkoutsPerWeek;
  private BigDecimal averageSleepHours;
  private Integer alcoholGlassesPerWeek;
  @Embedded
  private UserPhysicalCheckupResults physicalCheckupResults;

}
