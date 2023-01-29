package com.myhealth.healthmanagermain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.myhealth.healthmanagermain.domain.enums.ParameterType;
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
@Table(name = "lab_exam")
public class LabExam {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @Size(min = 1, max = 25)
  @Column(name = "parameter", length = 25)
  private String parameter;

  @NotNull
  @Column(name = "value")
  private Double value;

  @Enumerated(EnumType.STRING)
  @Column(name = "parameter_type", nullable = false)
  private ParameterType parameterType;

  @ManyToOne(optional = false)
  @JoinColumn(name = "exam_id")
  @NotNull
  @JsonIgnore
  @ToString.Exclude
  private Exam anExam;
}
