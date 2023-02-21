package com.myhealth.healthmanagermain.service.dto;

import com.myhealth.healthmanagermain.config.Constants;
import com.myhealth.healthmanagermain.domain.Authority;
import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.domain.enums.UserType;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Nullable
  private Long id;

  @NotBlank
  @Pattern(regexp = Constants.USERNAME_REGEX)
  @Size(min = 3, max = 50)
  private String username;

  @NotBlank
  @Size(min = 3, max = 50)
  private String firstName;

  @NotBlank
  @Size(min = 3, max = 50)
  private String lastName;

  @NotBlank
  @Email
  @Size(min = 5, max = 254)
  private String email;

  @NotBlank
  @Size(max = 256)
  private String imageUrl;

  private boolean activated;

  @NotBlank
  @Size(min = 2, max = 10)
  private String langKey;

  @NonNull
  private UserType type;

  @NonNull
  private LocalDate birthDate;

  @NonNull
  private Set<String> authorities = new HashSet<>();

  public AdminUserDTO(@NonNull UserAccount user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.email = user.getEmail();
    this.activated = user.isActivated();
    this.imageUrl = user.getImageUrl();
    this.langKey = user.getLangKey();
    this.type = user.getType();
    this.birthDate = user.getBirthDate();
    this.authorities = user.getAuthorities().stream().map(Authority::getName).collect(
        Collectors.toSet());
  }
}
