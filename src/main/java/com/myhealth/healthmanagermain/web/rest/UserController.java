package com.myhealth.healthmanagermain.web.rest;

import com.myhealth.healthmanagermain.config.Constants;
import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.security.AuthoritiesConstants;
import com.myhealth.healthmanagermain.service.UserManagementService;
import com.myhealth.healthmanagermain.service.domain.UserAccountService;
import com.myhealth.healthmanagermain.service.dto.AdminUserDTO;
import com.myhealth.healthmanagermain.web.rest.errors.BadRequestApiException;
import com.myhealth.healthmanagermain.web.rest.errors.EmailAlreadyUsedException;
import com.myhealth.healthmanagermain.web.rest.errors.LoginAlreadyUsedException;
import com.myhealth.healthmanagermain.web.rest.util.PaginationUtil;
import com.myhealth.healthmanagermain.web.rest.util.ResponseUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class UserController {

  private static final List<String> ALLOWED_ORDERED_PROPERTIES = List.of("id", "username",
      "firstName", "lastName", "email", "langKey", "createdDate");

  @NonNull
  private final UserManagementService userManagementService;
  @NonNull
  private final UserAccountService userAccountService;

  @PostMapping("/users")
  @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
  public ResponseEntity<UserAccount> createUserAccount(
      @Valid @RequestBody AdminUserDTO adminUserDTO)
      throws URISyntaxException {
    log.debug("REST request to save UserAccount : {}", adminUserDTO);
    if (adminUserDTO.getId() != null) {
      throw new BadRequestApiException("A new user cannot already have an ID", "userManagement",
          "idexists");
    } else if (userAccountService.getUserByUsername(adminUserDTO.getUsername()).isPresent()) {
      throw new LoginAlreadyUsedException();
    } else if (userAccountService.getUserByEmail(adminUserDTO.getEmail()).isPresent()) {
      throw new EmailAlreadyUsedException();
    } else {
      UserAccount newUser = userManagementService.createUser(adminUserDTO);
      //TODO: send account creation email
      return ResponseEntity
          .created(new URI("/api/admin/users/" + newUser.getUsername()))
          .body(newUser);
    }
  }

  @PutMapping("/users")
  @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
  public ResponseEntity<AdminUserDTO> updateUserAccount(
      @Valid @RequestBody AdminUserDTO adminUserDTO) {
    log.debug("REST request to update UserAccount : {}", adminUserDTO);
    Optional<UserAccount> existingUser = userAccountService.getUserByEmail(adminUserDTO.getEmail());
    if (existingUser.isPresent() && !existingUser.get().getId().equals(adminUserDTO.getId())) {
      throw new EmailAlreadyUsedException();
    }
    existingUser = userAccountService.getUserByUsername(adminUserDTO.getUsername());
    if (existingUser.isPresent() && !existingUser.get().getId().equals(adminUserDTO.getId())) {
      throw new LoginAlreadyUsedException();
    }
    Optional<AdminUserDTO> updatedUser = userManagementService.updateUser(adminUserDTO);

    return ResponseUtil.wrapOrNotFound(updatedUser);
  }

  @GetMapping("/users")
  @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
  public ResponseEntity<List<AdminUserDTO>> getAllUsers(
      @org.springdoc.api.annotations.ParameterObject Pageable pageable) {
    log.debug("REST request to get all UserAccount for an admin");
    if (!onlyContainsAllowedProperties(pageable)) {
      return ResponseEntity.badRequest().build();
    }

    final Page<AdminUserDTO> page = userAccountService.getAllManagedUsers(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
        ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
  }

  @GetMapping("/users/{username}")
  @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
  public ResponseEntity<AdminUserDTO> getUser(
      @PathVariable @Pattern(regexp = Constants.USERNAME_REGEX) String username) {
    log.debug("REST request to get UserAccount : {}", username);
    return ResponseUtil.wrapOrNotFound(
        userAccountService.getUserWithAuthoritiesByUsername(username).map(AdminUserDTO::new));
  }

  @DeleteMapping("/users/{username}")
  @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
  public ResponseEntity<Void> deleteUser(
      @PathVariable @Pattern(regexp = Constants.USERNAME_REGEX) String username) {
    log.debug("REST request to delete UserAccount: {}", username);
    userAccountService.deleteByUsername(username);
    return ResponseEntity.noContent().build();
  }

  private boolean onlyContainsAllowedProperties(Pageable pageable) {
    return pageable.getSort().stream().map(Sort.Order::getProperty)
        .allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
  }
}
