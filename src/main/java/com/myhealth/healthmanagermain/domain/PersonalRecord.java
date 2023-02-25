package com.myhealth.healthmanagermain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.myhealth.healthmanagermain.domain.enums.RecordType;
import com.myhealth.healthmanagermain.domain.enums.WeightSystem;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "personal_record")
public class PersonalRecord implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(min = 3, max = 100)
  @Column(name = "exercise", nullable = false)
  private String exercise;

  @NotNull
  @Column(name = "target", precision = 50, scale = 10)
  private BigDecimal target;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "weight_system", nullable = false)
  private WeightSystem weightSystem;

  @Enumerated(EnumType.STRING)
  @Column(name = "record_type", nullable = false)
  private RecordType recordType;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "workout_id")
  @NotNull
  @JsonIgnore
  @ToString.Exclude
  private Workout workout;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  @NotNull
  @JsonIgnore
  @ToString.Exclude
  private UserAccount user;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PersonalRecord other)) {
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
