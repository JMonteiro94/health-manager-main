package com.myhealth.healthmanagermain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ToString
@Setter
@Getter
@Builder
@Entity
@Table(name = "health_exam_file")
public class HealthExam implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String fileType;

    @NotNull
    @Lob
    private byte[] file;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exam_id")
    @NotNull
    @JsonIgnore
    @ToString.Exclude
    private Exam exam;

//    @Column(name = "description", length = 150)
//    @Lob()
//    private String description;
}
