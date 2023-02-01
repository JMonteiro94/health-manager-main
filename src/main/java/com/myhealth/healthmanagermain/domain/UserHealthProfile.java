package com.myhealth.healthmanagermain.domain;

import com.myhealth.healthmanagermain.domain.enums.Gender;
import com.myhealth.healthmanagermain.domain.enums.JobType;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_health_profile")
public class UserHealthProfile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender", nullable = false)
  private Gender gender;

  @NotNull
  @Column(name = "height")
  private Integer height;

  @NotNull
  @Column(name = "weight")
  private BigDecimal weight;

  @Enumerated(EnumType.STRING)
  @Column(name = "jobType", nullable = false)
  private JobType jobType;

  @Embedded
  private UserHabits habits;
}
