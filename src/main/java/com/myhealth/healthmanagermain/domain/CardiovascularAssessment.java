package com.myhealth.healthmanagermain.domain;

import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Embeddable
public class CardiovascularAssessment {

  private Integer systolicBloodPressure;
  private Integer diastolicBloodPressure;
  private Integer restingHearRate;

}
