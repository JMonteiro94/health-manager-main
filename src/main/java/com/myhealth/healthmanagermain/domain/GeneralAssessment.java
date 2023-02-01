package com.myhealth.healthmanagermain.domain;

import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Embeddable
public class GeneralAssessment {

  private Float weight;
  private Float bodyFat;
  private Float muscleMass;
  private Float boneMass;
  private Float bodyMass;
  private Float basalMetabolicRate;
  private Float metabolicAge;
  private Float totalWater;
  private Float visceralFat;
}
