package com.myhealth.healthmanagermain.service.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GenericDomainEntityService<T, ID> {

  Optional<T> getById(ID id);

  List<T> getAll();

  T save(T t);

  List<T> saveAll(Collection<T> collection);

  void delete(T t);

  void deleteAll();
  
}
