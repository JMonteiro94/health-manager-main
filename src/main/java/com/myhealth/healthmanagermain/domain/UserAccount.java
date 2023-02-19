package com.myhealth.healthmanagermain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.myhealth.healthmanagermain.config.Constants;
import com.myhealth.healthmanagermain.domain.enums.UserType;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
import org.springframework.data.annotation.CreatedDate;

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
  @Pattern(regexp = Constants.USERNAME_REGEX)
  @Size(min = 1, max = 50)
  @Column(length = 50, unique = true, nullable = false)
  private String username;

  @JsonIgnore
  @NotNull
  @Size(min = 60, max = 60)
  @Column(name = "password_hash", length = 60, nullable = false)
  private String password;

  @Size(max = 50)
  @Column(name = "first_name", length = 50)
  private String firstName;

  @Size(max = 50)
  @Column(name = "last_name", length = 50)
  private String lastName;

  @NotBlank
  @Email
  @Size(min = 5, max = 254)
  @Column(length = 254, unique = true)
  private String email;

  @NotNull
  @Column(nullable = false)
  private boolean activated = false;

  @Size(min = 2, max = 10)
  @Column(name = "lang_key", length = 10)
  private String langKey;

  @Size(max = 256)
  @Column(name = "image_url", length = 256)
  private String imageUrl;

  @JsonIgnore
  @Size(max = 20)
  @Column(name = "activation_key", length = 20)
  private String activationKey;

  @JsonIgnore
  @Size(max = 20)
  @Column(name = "reset_key", length = 20)
  private String resetKey;

  @Column(name = "reset_date")
  private Instant resetDate = null;

  @NotNull
  @Column(name = "birth_date")
  private LocalDate birthDate;

  @JsonIgnore
  @CreatedDate
  @Column(name = "created_date", updatable = false)
  private Instant createdDate = Instant.now();

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private UserType type;

  @Embedded
  private UserPreferences preferences;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
  @JsonIgnore
  @Exclude
  private Set<Workout> userWorkouts = new HashSet<>();

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
  @JsonIgnore
  @Exclude
  private Set<BodyMeasure> bodyMeasures = new HashSet<>();

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  @Exclude
  private Set<Meal> meals = new HashSet<>();

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  @Exclude
  private Set<ExerciseDefinition> exerciseDefinitions = new HashSet<>();

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  @Exclude
  private Set<Goal> goals = new HashSet<>();

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  @Exclude
  private Set<PersonalRecord> personalRecords = new HashSet<>();

  @JsonIgnore
  @ManyToMany(fetch = FetchType.EAGER)
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
    if (!(o instanceof UserAccount other)) {
      return false;
    }

    return id != null &&
        id.equals(other.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
