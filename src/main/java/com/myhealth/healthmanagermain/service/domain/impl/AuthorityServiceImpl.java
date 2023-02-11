package com.myhealth.healthmanagermain.service.domain.impl;

import com.myhealth.healthmanagermain.domain.Authority;
import com.myhealth.healthmanagermain.repository.AuthorityRepository;
import com.myhealth.healthmanagermain.service.domain.AuthorityService;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

  @NotNull
  private final AuthorityRepository authorityRepository;

  @Override
  @Transactional(readOnly = true)
  public Optional<Authority> findByName(String authority) {
    return authorityRepository.findById(authority);
  }

  @Override
  @Transactional(readOnly = true)
  public Set<String> getAll() {
    return authorityRepository.findAll().stream().map(Authority::getName)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public void saveAll(Set<String> authorities) {
    authorities.stream().map(Authority::new).forEach(authorityRepository::save);
  }
}
