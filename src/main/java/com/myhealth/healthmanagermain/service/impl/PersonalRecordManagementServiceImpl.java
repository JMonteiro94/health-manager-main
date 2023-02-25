package com.myhealth.healthmanagermain.service.impl;

import com.myhealth.healthmanagermain.domain.PersonalRecord;
import com.myhealth.healthmanagermain.domain.Workout;
import com.myhealth.healthmanagermain.service.PersonalRecordManagementService;
import com.myhealth.healthmanagermain.service.domain.PersonalRecordService;
import com.myhealth.healthmanagermain.util.converter.BigDecimalToString;
import com.myhealth.healthmanagermain.web.rest.dto.PersonalRecordDTO;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PersonalRecordManagementServiceImpl implements PersonalRecordManagementService {

  @NonNull
  private final PersonalRecordService personalRecordService;

  @Override
  public List<PersonalRecordDTO> getAllUserPersonalRecordsByUsername(@NonNull String username) {
    List<PersonalRecord> personalRecords = personalRecordService.getAllByUsername(username);
    return personalRecords.stream().map(pr -> this.map(pr, pr.getWorkout())).toList();
  }

  private PersonalRecordDTO map(@NonNull PersonalRecord personalRecord, @NonNull Workout workout) {
    return PersonalRecordDTO.builder()
        .exercise(personalRecord.getExercise())
        .recordType(personalRecord.getRecordType())
        .value(new BigDecimalToString().convert(personalRecord.getTarget()))
        .date(LocalDate.from(workout.getDate()))
        .workoutNumber(workout.getNumber())
        .build();
  }
}
