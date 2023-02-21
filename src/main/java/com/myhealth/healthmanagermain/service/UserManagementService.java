package com.myhealth.healthmanagermain.service;

import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.service.dto.AdminUserDTO;
import java.time.LocalDate;
import java.util.Optional;

public interface UserManagementService {

  UserAccount createUser(AdminUserDTO adminUserDTO);

  Optional<AdminUserDTO> updateUser(AdminUserDTO adminUserDTO);

  Optional<UserAccount> activateRegistration(String key);

  UserAccount registerUser(AdminUserDTO userDTO, String password);

  void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl,
      LocalDate birthDate);

  Optional<UserAccount> requestPasswordReset(String email);

  Optional<UserAccount> completePasswordReset(String newPassword, String key);

  void changeUserPassword(String oldPassword, String newPassword);

}
