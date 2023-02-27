package com.myhealth.healthmanagermain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.myhealth.healthmanagermain.domain.enums.GoalType;
import com.myhealth.healthmanagermain.domain.enums.TimeWindow;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
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
import javax.persistence.Table;
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
@Table(name = "goal")
public class Goal implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(min = 3, max = 100)
  @Column(name = "name", nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "goal_type", nullable = false)
  private GoalType goalType;

  @NotNull
  @Column(name = "target_value", nullable = false)
  private BigDecimal targetValue;

  @Enumerated(EnumType.STRING)
  @Column(name = "window_time", nullable = false)
  private TimeWindow windowTime;

  @NotNull
  @Column(name = "window_number", nullable = false)
  private Integer windowNumber;

  @Nullable
  @Column(name = "start_date")
  private ZonedDateTime startDate;

  @Nullable
  @Column(name = "finish_date")
  private ZonedDateTime finishDate;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  @NotNull
  @JsonIgnore
  @ToString.Exclude
  private UserAccount user;

  @ManyToOne(optional = false)
  @JoinColumn(name = "exercise_definition_id")
  @NotNull
  @JsonIgnore
  @ToString.Exclude
  private ExerciseDefinition exerciseDefinition;
}
