package com.myhealth.healthmanagermain.service.domain.impl;

import com.myhealth.healthmanagermain.service.domain.GenericDomainEntityService;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class GenericDomainEntityServiceImpl<T, R extends JpaRepository<T, Long>> implements
    GenericDomainEntityService<T, Long> {

  @NonNull
  private final R repo;

  @Transactional(readOnly = true)
  public Optional<T> getById(Long id) {
    return repo.findById(id);
  }

  @Transactional(readOnly = true)
  public List<T> getAll() {
    return repo.findAll();
  }

  @Transactional
  public T save(T t) {
    return repo.save(t);
  }

  @Transactional
  public List<T> saveAll(Collection<T> collection) {
    return repo.saveAll(collection);
  }

  @Transactional
  public void delete(T t) {
    repo.delete(t);
  }

  @Transactional
  public void deleteAll() {
    repo.deleteAll();
  }
}
