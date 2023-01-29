package com.myhealth.healthmanagermain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.myhealth.healthmanagermain.config.Constants;
import com.myhealth.healthmanagermain.domain.enums.UserType;
import java.io.Serializable;
import java.time.Instant;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.BatchSize;

@ToString
@Setter
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_account")
public class UserAccount implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Pattern(regexp = Constants.LOGIN_REGEX)
  @Size(min = 1, max = 50)
  @Column(length = 50, unique = true, nullable = false)
  private String username;

  @JsonIgnore
  @NotNull
  @Size(min = 60, max = 60)
  @Column(name = "password_hash", length = 60, nullable = false)
  private String password;

  @NotNull
  @Size(max = 50)
  @Column(name = "first_name", length = 50)
  private String firstName;

  @Size(max = 50)
  @Column(name = "last_name", length = 50)
  private String lastName;

  @Email
  @Size(min = 5, max = 254)
  @Column(length = 254, unique = true)
  private String email;

  @NotNull
  @Column(nullable = false)
  private boolean activated = false;

  @Size(min = 2, max = 6)
  @Column(name = "lang_key", length = 6)
  private String langKey;

  @Size(max = 256)
  @Column(name = "image_url", length = 256)
  private String imageUrl;

  @NotNull
  @Column(name = "birth_date")
  @Temporal(TemporalType.DATE)
  private Calendar birthDate;

  @Column(name = "last_login")
  private Instant lastLogin;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private UserType userType;

  @Embedded
  private UserPreferences preferences;

  @ManyToOne(optional = false)
  @JoinColumn(name = "center_id")
  @Null
  @JsonIgnore
  @ToString.Exclude
  private Center center;

  @JsonIgnore
  @ManyToMany
  @JoinTable(
      name = "user_authority",
      joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
  @BatchSize(size = 20)
  @Exclude
  private Set<Authority> authorities = new HashSet<>();

  public void setUsername(String username) {
    this.username = StringUtils.lowerCase(username, Locale.ENGLISH);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    UserAccount userAccount = (UserAccount) o;
    return !(userAccount.getId() == null || getId() == null) && Objects.equals(getId(),
        userAccount.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}
