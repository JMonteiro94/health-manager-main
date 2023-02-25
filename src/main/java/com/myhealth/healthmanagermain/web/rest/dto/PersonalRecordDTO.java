package com.myhealth.healthmanagermain.web.rest.dto;

import com.myhealth.healthmanagermain.domain.enums.RecordType;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalRecordDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;
  
  private String exercise;

  private String value;

  private RecordType recordType;

  private Long workoutNumber;

  private LocalDate date;
}
