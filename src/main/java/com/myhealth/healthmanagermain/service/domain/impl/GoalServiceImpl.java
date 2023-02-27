package com.myhealth.healthmanagermain.service.domain.impl;

import com.myhealth.healthmanagermain.domain.Goal;
import com.myhealth.healthmanagermain.repository.GoalRepository;
import com.myhealth.healthmanagermain.service.domain.GoalService;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GoalServiceImpl extends GenericDomainEntityServiceImpl<Goal, GoalRepository>
    implements GoalService {

  @NonNull
  private final GoalRepository repo;

  public GoalServiceImpl(@NonNull GoalRepository repo) {
    super(repo);
    this.repo = repo;
  }

  @Override
  public List<Goal> getAllByUserId(Long userId) {
    return repo.findAllByUserId(userId);
  }

  @Override
  public List<Goal> getAllByUsername(String username) {
    return repo.findAllByUserUsername(username);
  }
}
