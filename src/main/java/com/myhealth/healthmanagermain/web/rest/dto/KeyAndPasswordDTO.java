package com.myhealth.healthmanagermain.web.rest.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyAndPasswordDTO {

  @NotBlank
  private String key;
  private String newPassword;
}
