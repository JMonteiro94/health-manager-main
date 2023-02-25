package com.myhealth.healthmanagermain.service.domain;

import com.myhealth.healthmanagermain.domain.PersonalRecord;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PersonalRecordService {

  Optional<PersonalRecord> getById(Long id);

  List<PersonalRecord> getAllByUserId(Long id);

  List<PersonalRecord> getAllByUsername(String username);

  PersonalRecord save(PersonalRecord personalRecord);

  List<PersonalRecord> saveAll(Collection<PersonalRecord> personalRecord);

  void delete(PersonalRecord personalRecord);

}
