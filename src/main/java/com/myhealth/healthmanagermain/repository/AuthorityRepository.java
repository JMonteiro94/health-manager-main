package com.myhealth.healthmanagermain.repository;

import com.myhealth.healthmanagermain.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
