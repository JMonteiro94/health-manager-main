package com.myhealth.healthmanagermain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
@Table(name = "aerobic_exercise")
public class AerobicExercise {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(min = 1, max = 100)
  @Column(length = 100)
  private String name;

  @NotNull
  @Column(name = "distance")
  private Integer distance;

  @NotNull
  @Column(name = "duration")
  private Integer duration;

  @Min(1)
  @Max(100)
  @NotNull
  @Column(name = "intensity")
  private Integer intensity;

  @NotNull
  @Column(name = "calories")
  private Integer calories;

  @NotNull
  @Column(name = "average_heart_Rate")
  private Integer averageHeartRate;

  @ManyToOne(optional = false)
  @JoinColumn(name = "workout_id")
  @NotNull
  @JsonIgnore
  @ToString.Exclude
  private Workout workout;

}
