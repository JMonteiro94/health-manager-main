package com.myhealth.healthmanagermain.web.rest;

import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.security.SecurityUtils;
import com.myhealth.healthmanagermain.service.UserManagementService;
import com.myhealth.healthmanagermain.service.domain.impl.UserAccountServiceImpl;
import com.myhealth.healthmanagermain.service.dto.AdminUserDTO;
import com.myhealth.healthmanagermain.service.dto.PasswordChangeDTO;
import com.myhealth.healthmanagermain.web.rest.dto.KeyAndPasswordDTO;
import com.myhealth.healthmanagermain.web.rest.dto.ManagedUserDTO;
import com.myhealth.healthmanagermain.web.rest.errors.AccountResourceException;
import com.myhealth.healthmanagermain.web.rest.errors.EmailAlreadyUsedException;
import com.myhealth.healthmanagermain.web.rest.errors.InvalidPasswordException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class AccountController {

  @NonNull
  private final UserManagementService userManagementService;
  @NonNull
  private final UserAccountServiceImpl userService;


  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public void registerAccount(@Valid @RequestBody ManagedUserDTO managedUserDTO) {
    if (ManagedUserDTO.isPasswordLengthInvalid(managedUserDTO.getPassword())) {
      throw new InvalidPasswordException();
    }
    userManagementService.registerUser(managedUserDTO,
        managedUserDTO.getPassword());
    //TODO: send account activation email
  }


  @GetMapping("/activate")
  public void activateAccount(@RequestParam(value = "key") String key) {
    Optional<UserAccount> userAccount = userManagementService.activateRegistration(key);
    if (userAccount.isEmpty()) {
      throw new AccountResourceException("No UserAccount was found for this activation key");
    }
  }

  @GetMapping("/authenticate")
  public String isAuthenticated(HttpServletRequest request) {
    log.debug("REST request to check if the current UserAccount is authenticated");
    return request.getRemoteUser();
  }


  @GetMapping("/account")
  public AdminUserDTO getAccount() {
    return userService
        .getUserWithAuthorities()
        .map(AdminUserDTO::new)
        .orElseThrow(() -> new AccountResourceException("UserAccount could not be found"));
  }

  @PostMapping("/account")
  public void saveAccount(@Valid @RequestBody AdminUserDTO userDTO) {
    String userUsername = SecurityUtils
        .getCurrentUserLogin()
        .orElseThrow(() -> new AccountResourceException("Current UserAccount username not found"));
    Optional<UserAccount> existingUser = userService.getUserByEmail(
        userDTO.getEmail());
    if (existingUser.isPresent() && !existingUser.get().getUsername()
        .equalsIgnoreCase(userUsername)) {
      throw new EmailAlreadyUsedException();
    }
    Optional<UserAccount> userAccount = userService.getUserByUsername(userUsername);
    if (userAccount.isEmpty()) {
      throw new AccountResourceException("UserAccount could not be found");
    }
    userManagementService.updateUser(
        userDTO.getFirstName(),
        userDTO.getLastName(),
        userDTO.getEmail(),
        userDTO.getLangKey(),
        userDTO.getImageUrl(),
        userDTO.getBirthDate()
    );
  }

  @PostMapping(path = "/account/change-password")
  public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
    if (ManagedUserDTO.isPasswordLengthInvalid(passwordChangeDto.getNewPassword())
        || ManagedUserDTO.isPasswordLengthInvalid(passwordChangeDto.getCurrentPassword())) {
      throw new InvalidPasswordException();
    }
    userManagementService.changeUserPassword(passwordChangeDto.getCurrentPassword(),
        passwordChangeDto.getNewPassword());
  }

  @PostMapping(path = "/account/reset-password/init")
  public void requestPasswordReset(@RequestBody String email) {
    Optional<UserAccount> userAccount = userManagementService.requestPasswordReset(email);
    if (userAccount.isPresent()) {
      //TODO send reset password email
    } else {
      log.warn(String.format("Password reset requested for non existing email %s", email));
    }
  }

  @PostMapping(path = "/account/reset-password/finish")
  public void finishPasswordReset(@Valid @RequestBody KeyAndPasswordDTO keyAndPasswordDTO) {
    if (ManagedUserDTO.isPasswordLengthInvalid(keyAndPasswordDTO.getNewPassword())) {
      throw new InvalidPasswordException();
    }
    Optional<UserAccount> userAccount = userManagementService.completePasswordReset(
        keyAndPasswordDTO.getNewPassword(), keyAndPasswordDTO.getKey());

    if (userAccount.isEmpty()) {
      throw new AccountResourceException("No user account was found for this reset key");
    }
  }
}
