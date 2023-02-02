package com.myhealth.healthmanagermain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "meal_food_details")
public class MealFoodDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(min = 3, max = 100)
  @Column(name = "name", nullable = false)
  private String name;

  @Nullable
  @Column(name = "quantity")
  private BigDecimal quantity;

  @Nullable
  @Column(name = "quantity_unit")
  private String quantityUnit;

  @Nullable
  @Column(name = "carbs")
  private Integer carbs;

  @Nullable
  @Column(name = "fats")
  private Integer fats;

  @Nullable
  @Column(name = "proteins")
  private BigDecimal proteins;

  @ManyToOne(optional = false)
  @JoinColumn(name = "meal_id")
  @NotNull
  @JsonIgnore
  @ToString.Exclude
  private Meal meal;
}
