package com.myhealth.healthmanagermain.service.domain.impl;

import com.myhealth.healthmanagermain.domain.Workout;
import com.myhealth.healthmanagermain.repository.WorkoutRepository;
import com.myhealth.healthmanagermain.service.domain.WorkoutService;
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
public class WorkoutServiceImpl implements WorkoutService {

  @NonNull
  private final WorkoutRepository repo;

  @Override
  @Transactional(readOnly = true)
  public Optional<Workout> getById(Long id) {
    return repo.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Workout> getAllByUserId(Long userId) {
    return repo.findAllByUserId(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Workout> getAllByUsername(String username) {
    return repo.findAllByUserUsername(username);
  }

  @Override
  @Transactional
  public Workout save(Workout workout) {
    return repo.save(workout);
  }

  @Override
  @Transactional
  public Collection<Workout> saveAll(Collection<Workout> workouts) {
    return repo.saveAll(workouts);
  }

  @Override
  @Transactional
  public void delete(Workout workout) {
    repo.delete(workout);
  }
}
