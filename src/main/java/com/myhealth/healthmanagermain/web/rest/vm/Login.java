package com.myhealth.healthmanagermain.web.rest.vm;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public final class Login {

  @NotBlank
  @Size(min = 1, max = 50)
  private String username;

  @NotBlank
  @Size(min = 4, max = 100)
  private String password;

  private boolean rememberMe;
}
