package com.myhealth.healthmanagermain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.myhealth.healthmanagermain.domain.enums.WorkoutType;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;

@ToString
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workout")
public class Workout {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private WorkoutType type;

  @NotNull
  @Column(name = "start_date")
  @Temporal(TemporalType.DATE)
  private Calendar startDate;

  @NotNull
  @Column(name = "finish_date")
  @Temporal(TemporalType.DATE)
  private Calendar finishDate;

  @NotNull
  @Column(name = "starting_weight")
  private Double startingWeight;

  @NotNull
  @Column(name = "finish_weight")
  private Double finishWeight;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private DayOfWeek weekDay;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  @NotNull
  @JsonIgnore
  @ToString.Exclude
  private UserAccount user;

  @OneToMany(mappedBy = "workout")
  @Exclude
  private Set<AerobicExercise> aerobicExercises;

  @OneToMany(mappedBy = "workout")
  @Exclude
  private Set<AnaerobicExercise> anaerobicExercises;
}


