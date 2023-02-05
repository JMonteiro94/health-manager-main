package com.myhealth.healthmanagermain.repository;

import com.myhealth.healthmanagermain.domain.UserAccount;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

  String USERS_BY_USERNAME_CACHE = "usersByUsername";

  Optional<UserAccount> findOneByEmailIgnoreCase(String email);

  Optional<UserAccount> findOneByUsername(String login);

  @EntityGraph(attributePaths = "authorities")
  Optional<UserAccount> findOneWithAuthoritiesById(Long id);

  @EntityGraph(attributePaths = "authorities")
  @Cacheable(cacheNames = USERS_BY_USERNAME_CACHE)
  Optional<UserAccount> findOneWithAuthoritiesByUsername(String username);

  Page<UserAccount> findAllByUsernameNot(Pageable pageable, String login);
}
