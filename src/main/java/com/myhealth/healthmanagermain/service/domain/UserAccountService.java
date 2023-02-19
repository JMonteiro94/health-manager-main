package com.myhealth.healthmanagermain.service.domain;

import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.service.dto.AdminUserDTO;
import com.myhealth.healthmanagermain.service.dto.UserAccountDTO;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface UserAccountService {

  Optional<UserAccount> getUserById(Long id);

  Optional<UserAccount> getUserByUsername(String username);

  Optional<UserAccount> getUserByEmail(String email);

  Page<AdminUserDTO> getAllManagedUsers(Pageable pageable);

  Page<UserAccountDTO> getAllPublicUsers(Pageable pageable);

  Optional<UserAccount> getUserWithAuthorities();

  Optional<UserAccount> getUserWithAuthoritiesByEmail(String email);

  Optional<UserAccount> getUserWithAuthoritiesByUsername(String username);

  void deleteByUsername(String username);

  void deleteUser(UserAccount userAccount);

  void save(UserAccount userAccount);

  @Transactional
  void saveAndFlush(@NonNull UserAccount userAccount);

  Optional<UserAccount> getUserByResetKey(String key);

  Optional<UserAccount> getUserByActivationKey(String key);

  void activateUser(UserAccount userAccount);

  void resetUserKey(UserAccount userAccount);

  void resetUserPassword(UserAccount userAccount, String password);

  void updateUserPassword(UserAccount userAccount, String oldPassword, String newPassword);

  List<UserAccount> getAllDeactivatedUsersCreatedBeforeXDays(int days);

  void deleteAll();

  List<UserAccount> getAll();
}
