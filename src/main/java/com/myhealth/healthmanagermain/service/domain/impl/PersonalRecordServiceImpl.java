package com.myhealth.healthmanagermain.service.domain.impl;

import com.myhealth.healthmanagermain.domain.PersonalRecord;
import com.myhealth.healthmanagermain.repository.PersonalRecordRepository;
import com.myhealth.healthmanagermain.service.domain.PersonalRecordService;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class PersonalRecordServiceImpl implements PersonalRecordService {

  @NonNull
  private final PersonalRecordRepository repo;

  @Override
  @Transactional(readOnly = true)
  public Optional<PersonalRecord> getById(@NonNull Long id) {
    return repo.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PersonalRecord> getAllByUserId(@NonNull Long userId) {
    return repo.findAllByUserId(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PersonalRecord> getAllByUsername(@NonNull String username) {
    return repo.findAllByUserUsername(username);
  }

  @Override
  @Transactional
  public PersonalRecord save(@NonNull PersonalRecord personalRecord) {
    return repo.save(personalRecord);
  }

  @Override
  @Transactional
  public List<PersonalRecord> saveAll(@NonNull Collection<PersonalRecord> personalRecords) {
    return repo.saveAll(personalRecords);
  }

  @Override
  @Transactional
  public void delete(PersonalRecord personalRecord) {
    repo.deleteById(personalRecord.getId());
  }
}
