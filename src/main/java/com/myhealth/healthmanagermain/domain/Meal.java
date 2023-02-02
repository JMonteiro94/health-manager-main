package com.myhealth.healthmanagermain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Calendar;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "meal")
public class Meal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(min = 3, max = 100)
  @Column(name = "place", nullable = false)
  private String place;

  @NotNull
  @Column(name = "date")
  @Temporal(TemporalType.DATE)
  private Calendar date;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  @NotNull
  @JsonIgnore
  @ToString.Exclude
  private UserAccount user;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "meal")
  @Exclude
  private Set<MealFoodDetails> mealFoodDetails;
}
