package com.myhealth.healthmanagermain.domain;

import javax.persistence.Embeddable;
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
public class UserPhysicalCheckupResults {

  private Integer testosteroneLevel;
  private Integer estrogenLevel;
  private Integer bloodPressure;
  private Integer restingHeartRate;
  private Integer hdlCholesterol;
  private Integer ldlCholesterol;
  private Integer triglycerides;
  private Integer fastingBloodSugar;

}
