package com.myhealth.healthmanagermain.domain;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Embeddable
public class UserHabits {

  private Boolean isSmoker;
  private Integer cigPerDay;
  private Integer smokeYears;
  private Integer averageWorkoutsPerWeek;
  private Float averageSleepHours;
  private Integer alcoholGlassesPerWeek;
  @Embedded
  private UserPhysicalCheckupResults physicalCheckupResults;

}
