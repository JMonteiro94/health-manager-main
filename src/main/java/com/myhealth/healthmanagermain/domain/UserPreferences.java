package com.myhealth.healthmanagermain.domain;

import com.myhealth.healthmanagermain.domain.enums.Currency;
import com.myhealth.healthmanagermain.domain.enums.Language;
import com.myhealth.healthmanagermain.domain.enums.WeightSystem;
import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Embeddable
public class UserPreferences {

  private WeightSystem weightSystem;
  private Language language;
  private Currency currency;
  private String country;

}
