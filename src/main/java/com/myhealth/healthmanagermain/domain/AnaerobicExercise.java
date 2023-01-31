package com.myhealth.healthmanagermain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
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
@Table(name = "anaerobic_exercise")
public class AnaerobicExercise {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(min = 1, max = 100)
  @Column(length = 100)
  private String name;

  @NotNull
  @Column(name = "muscle_group")
  private String muscleGroup;

  @NotNull
  @Column(name = "sets")
  private Integer sets;

  @NotNull
  @Column(name = "reps")
  private Integer reps;

  @Min(1)
  @Max(100)
  @NotNull
  @Column(name = "intensity")
  private Integer intensity;

  @NotNull
  @Column(name = "rest_interval")
  private Integer restInterval;

  @NotNull
  @Lob
  private byte[] note;

  @ManyToOne(optional = false)
  @JoinColumn(name = "workout_id")
  @NotNull
  @JsonIgnore
  @ToString.Exclude
  private Workout workout;
}
