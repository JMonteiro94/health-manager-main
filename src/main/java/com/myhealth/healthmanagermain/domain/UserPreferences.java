package com.myhealth.healthmanagermain.domain;

import com.myhealth.healthmanagermain.domain.enums.Currency;
import com.myhealth.healthmanagermain.domain.enums.Language;
import com.myhealth.healthmanagermain.domain.enums.WeightSystem;
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
public class UserPreferences {

  private WeightSystem weightSystem;
  private Language language;
  private Currency currency;
  private String country;

}
