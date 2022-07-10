package com.myhealth.healthmanagermain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.myhealth.healthmanagermain.domain.enums.CenterType;
import com.myhealth.healthmanagermain.domain.enums.ExamsType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Set;

@ToString
@Setter
@Getter
@Builder
@Entity
@Table(name = "exam")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "perform_date")
    private Instant performDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type", nullable = false)
    private ExamsType examType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    @NotNull
    @JsonIgnore
    @ToString.Exclude
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "anExam")
    @JsonIgnore
    private Set<LabExam> labExams;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "anExam")
    @JsonIgnore
    private Set<HealthExam> healthExams;
}
