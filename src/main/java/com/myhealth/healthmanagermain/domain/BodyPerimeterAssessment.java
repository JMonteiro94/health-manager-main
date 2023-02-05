package com.myhealth.healthmanagermain.domain;

import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Embeddable
public class BodyPerimeterAssessment {

  private Float neck;
  private Float rightArm;
  private Float leftArm;
  private Float chest;
  private Float waist;
  private Float hip;
  private Float abdominal;
  private Float rightThigh;
  private Float leftThigh;
  private Float rightCalf;
  private Float leftCalf;
}
