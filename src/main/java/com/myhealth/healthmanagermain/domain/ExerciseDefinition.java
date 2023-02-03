package com.myhealth.healthmanagermain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
@Table(name = "exercise_definition")
public class ExerciseDefinition {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(min = 2, max = 100)
  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "muscle_groups", columnDefinition = "text")
  private String muscleGroups;

  @OneToMany(mappedBy = "exerciseDefinition")
  @Exclude
  private Set<Goal> goal;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  @NotNull
  @JsonIgnore
  @ToString.Exclude
  private UserAccount user;
  
}
