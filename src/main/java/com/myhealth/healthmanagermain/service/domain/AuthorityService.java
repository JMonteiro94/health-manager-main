package com.myhealth.healthmanagermain.service.domain;

import com.myhealth.healthmanagermain.domain.Authority;
import java.util.Optional;
import java.util.Set;

public interface AuthorityService {

  Optional<Authority> findByName(String string);

  Set<String> getAll();

  void saveAll(Set<String> authorities);

}
