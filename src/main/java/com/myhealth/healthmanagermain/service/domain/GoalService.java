package com.myhealth.healthmanagermain.service.domain;

import com.myhealth.healthmanagermain.domain.Goal;
import java.util.List;

public interface GoalService extends GenericDomainEntityService<Goal, Long> {

  List<Goal> getAllByUserId(Long userId);

  List<Goal> getAllByUsername(String username);
}
