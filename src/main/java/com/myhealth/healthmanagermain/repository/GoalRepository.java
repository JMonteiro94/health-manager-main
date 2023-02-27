package com.myhealth.healthmanagermain.repository;

import com.myhealth.healthmanagermain.domain.Goal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {

  List<Goal> findAllByUserId(Long userId);

  List<Goal> findAllByUserUsername(String username);
}
