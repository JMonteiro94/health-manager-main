package com.myhealth.healthmanagermain.web.rest.dto;

import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.service.dto.AdminUserDTO;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@NoArgsConstructor
public class ManagedUserDTO extends AdminUserDTO {

  public static final int PASSWORD_MIN_LENGTH = 4;

  public static final int PASSWORD_MAX_LENGTH = 100;

  @NotBlank(message = "Password is null or empty")
  @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
  private String password;

  @Override
  public String toString() {
    return "ManagedUserVM{" + super.toString() + "} ";
  }

  public static boolean isPasswordLengthInvalid(String password) {
    return (StringUtils.isBlank(password) ||
        password.length() < ManagedUserDTO.PASSWORD_MIN_LENGTH ||
        password.length() > ManagedUserDTO.PASSWORD_MAX_LENGTH);
  }

  public static ManagedUserDTO fromUserAccount(UserAccount userAccount) {
    ManagedUserDTO managedUserDTO = new ManagedUserDTO();
    managedUserDTO.setId(userAccount.getId());
    managedUserDTO.setUsername(userAccount.getUsername());
    managedUserDTO.setPassword(userAccount.getPassword());
    managedUserDTO.setFirstName(userAccount.getFirstName());
    managedUserDTO.setLastName(userAccount.getLastName());
    managedUserDTO.setEmail(userAccount.getEmail());
    managedUserDTO.setActivated(userAccount.isActivated());
    managedUserDTO.setImageUrl(userAccount.getImageUrl());
    managedUserDTO.setLangKey(userAccount.getLangKey());
    managedUserDTO.setType(userAccount.getType());
    managedUserDTO.setBirthDate(userAccount.getBirthDate());
    return managedUserDTO;
  }
}
