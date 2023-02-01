package com.myhealth.healthmanagermain.domain;

import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Embeddable
public class SkinFoldAssessment {

  private Float triceps;
  private Float bicep;
  private Float subScapular;
  private Float iliocrystal;
  private Float surpraespinal;
  private Float abdominal;
}
