package com.myhealth.healthmanagermain.repository;

import com.myhealth.healthmanagermain.domain.UserAccount;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

  String USERS_BY_USERNAME_CACHE = "usersByLogin";

  String USERS_BY_EMAIL_CACHE = "usersByEmail";

  Optional<UserAccount> findOneByActivationKey(String activationKey);

  List<UserAccount> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
      Instant dateTime);

  Optional<UserAccount> findOneByResetKey(String resetKey);

  Optional<UserAccount> findOneByEmailIgnoreCase(String email);

  Optional<UserAccount> findOneByUsername(String username);

  @EntityGraph(attributePaths = "authorities")
  @Cacheable(cacheNames = USERS_BY_USERNAME_CACHE)
  Optional<UserAccount> findOneWithAuthoritiesByUsername(String username);

  @EntityGraph(attributePaths = "authorities")
  @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
  Optional<UserAccount> findOneWithAuthoritiesByEmailIgnoreCase(String email);

  Page<UserAccount> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);
}
