package com.myhealth.healthmanagermain.repository;

import com.myhealth.healthmanagermain.domain.Workout;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

  List<Workout> findAllByUserId(Long userId);

  List<Workout> findAllByUserUsername(String username);
}
