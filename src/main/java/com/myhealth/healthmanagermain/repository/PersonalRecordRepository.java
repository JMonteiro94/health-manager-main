package com.myhealth.healthmanagermain.repository;

import com.myhealth.healthmanagermain.domain.PersonalRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalRecordRepository extends JpaRepository<PersonalRecord, Long> {

  List<PersonalRecord> findAllByUserId(Long userId);

  List<PersonalRecord> findAllByUserUsername(String username);
}
