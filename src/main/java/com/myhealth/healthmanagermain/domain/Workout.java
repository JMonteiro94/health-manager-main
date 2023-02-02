package com.myhealth.healthmanagermain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.myhealth.healthmanagermain.domain.enums.WorkoutType;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Set;
import javax.annotation.Nullable;
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
import javax.validation.constraints.Size;
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

  @NotNull
  @Size(min = 3, max = 100)
  @Column(name = "place", nullable = false)
  private String place;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private WorkoutType type;

  @NotNull
  @Column(name = "date")
  @Temporal(TemporalType.DATE)
  private Calendar date;

  @Nullable
  @Column(name = "starting_weight")
  private BigDecimal startingWeight;

  @Nullable
  @Column(name = "finish_weight")
  private BigDecimal finishWeight;

  @Enumerated(EnumType.STRING)
  @Column(name = "week_day", nullable = false)
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


