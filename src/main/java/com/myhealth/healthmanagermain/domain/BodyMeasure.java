package com.myhealth.healthmanagermain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.DayOfWeek;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Embedded;
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

@ToString
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "body_measure")
public class BodyMeasure {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(min = 1, max = 100)
  @Column(name = "machine", length = 100)
  private String machine;

  @NotNull
  @Temporal(TemporalType.DATE)
  @Column(name = "date")
  private Calendar date;

  @Enumerated(EnumType.STRING)
  @Column(name = "week_day", nullable = false)
  private DayOfWeek weekDay;

  @Embedded
  private GeneralAssessment generalAssessment;

  @Embedded
  private CardiovascularAssessment cardiovascularAssessment;

  @Embedded
  private SkinFoldAssessment skinFoldAssessment;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @NotNull
  @JsonIgnore
  @ToString.Exclude
  private UserAccount user;

}
