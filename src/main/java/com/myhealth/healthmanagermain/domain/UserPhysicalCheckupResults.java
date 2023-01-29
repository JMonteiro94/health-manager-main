package com.myhealth.healthmanagermain.domain;

import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Embeddable
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
