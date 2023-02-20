package com.myhealth.healthmanagermain.service.dto;

import com.myhealth.healthmanagermain.domain.UserAccount;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountDTO implements Serializable {

  private Long id;
  @NonNull
  private String username;

  private boolean activated = true;

  @NonNull
  private String email;

  @NonNull
  private String langKey;

  public UserAccountDTO(@NonNull UserAccount user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.email = user.getEmail();
    this.langKey = user.getLangKey();
  }
}
