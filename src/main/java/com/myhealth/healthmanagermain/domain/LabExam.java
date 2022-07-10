package com.myhealth.healthmanagermain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.myhealth.healthmanagermain.domain.enums.ParameterType;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    private Float value;

    @Enumerated(EnumType.STRING)
    @Column(name = "parameter_type", nullable = false)
    private ParameterType parameterType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exam_id")
    @NotNull
    @JsonIgnore
    @ToString.Exclude
    private Exam exam;
}
