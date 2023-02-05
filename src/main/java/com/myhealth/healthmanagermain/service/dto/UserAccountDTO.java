package com.myhealth.healthmanagermain.service.dto;

import com.myhealth.healthmanagermain.config.Constants;
import com.myhealth.healthmanagermain.domain.Authority;
import com.myhealth.healthmanagermain.domain.UserAccount;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserAccountDTO {

  private Long id;

  @NotBlank
  @Pattern(regexp = Constants.LOGIN_REGEX)
  @Size(min = 1, max = 50)
  private String username;

  @Size(max = 50)
  private String firstName;

  @Size(max = 50)
  private String lastName;

  @Email
  @Size(min = 5, max = 254)
  private String email;

  @Size(max = 256)
  private String imageUrl;

  private boolean activated = false;

  @Size(min = 2, max = 6)
  private String langKey;

  private Set<String> authorities;

  public UserAccountDTO(UserAccount userAccount) {
    this.id = userAccount.getId();
    this.username = userAccount.getUsername();
    this.firstName = userAccount.getFirstName();
    this.lastName = userAccount.getLastName();
    this.activated = userAccount.isActivated();
    this.email = userAccount.getEmail();
    this.imageUrl = userAccount.getImageUrl();
    this.langKey = userAccount.getLangKey();
    this.authorities = userAccount.getAuthorities().stream()
        .map(Authority::getName)
        .collect(Collectors.toSet());
  }

}
