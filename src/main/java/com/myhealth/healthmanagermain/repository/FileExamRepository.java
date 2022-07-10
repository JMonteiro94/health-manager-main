package com.myhealth.healthmanagermain.repository;

import com.myhealth.healthmanagermain.domain.HealthExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileExamRepository extends JpaRepository<HealthExam, Long> {
}
