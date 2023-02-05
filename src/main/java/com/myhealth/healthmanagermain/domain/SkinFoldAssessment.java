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

  private Float tricepsFold;
  private Float bicepFold;
  private Float subScapularFold;
  private Float iliocrystalFold;
  private Float surpraespinalFold;
  private Float abdominalFold;
}
