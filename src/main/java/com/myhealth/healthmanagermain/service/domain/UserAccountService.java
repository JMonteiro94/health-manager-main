package com.myhealth.healthmanagermain.service.domain;

import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.service.dto.UserAccountDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserAccountService {

  void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl);

  Optional<UserAccountDTO> updateUser(UserAccountDTO userAccountDTO);

  void deleteUser(String login);

  Page<UserAccountDTO> getAllManagedUsers(Pageable pageable);

  Optional<UserAccount> getUserWithAuthoritiesByLogin(String login);

  Optional<UserAccount> getUserWithAuthorities(Long id);

  Optional<UserAccount> getUserWithAuthorities();

  List<String> getAuthorities();

  Optional<UserAccount> getUserByEmail(String email);

  UserAccount createUser(UserAccountDTO userAccountDTO);
}
