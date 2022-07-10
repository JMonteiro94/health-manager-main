package com.myhealth.healthmanagermain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.myhealth.healthmanagermain.config.Constants;
import com.myhealth.healthmanagermain.domain.enums.CenterType;
import com.myhealth.healthmanagermain.domain.enums.UserType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@ToString
@Setter
@Getter
@Builder
@Entity
@Table(name = "center")
public class Center {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String name;

    @Size(min = 1, max = 100)
    @Column(length = 1000)
    private String streetAddress;

    @Size(min = 1, max = 25)
    @Column(length = 25)
    private String district;

    @Size(min = 1, max = 25)
    @Column(length = 25)
    private String city;

    @Size(min = 1, max = 25)
    @Column(length = 25)
    private String parish;

    @Size(min = 1, max = 25)
    @Column(length = 25)
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "center_type", nullable = false)
    private CenterType centerType;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "anCenter")
    @JsonIgnore
    private Set<User> people;

}
