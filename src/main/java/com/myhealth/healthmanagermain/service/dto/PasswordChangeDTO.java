package com.myhealth.healthmanagermain.service.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private String currentPassword;
  private String newPassword;

}
