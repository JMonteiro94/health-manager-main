package com.myhealth.healthmanagermain.service.domain;

import com.myhealth.healthmanagermain.domain.Workout;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WorkoutService {

  Optional<Workout> getById(Long id);

  List<Workout> getAllByUserId(Long id);

  List<Workout> getAllByUsername(String username);

  Workout save(Workout workout);

  Collection<Workout> saveAll(Collection<Workout> workouts);

  void delete(Workout workout);

}
