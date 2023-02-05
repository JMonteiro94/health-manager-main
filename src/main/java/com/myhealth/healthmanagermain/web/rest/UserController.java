package com.myhealth.healthmanagermain.web.rest;


import com.myhealth.healthmanagermain.config.Constants;
import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.security.AuthoritiesConstants;
import com.myhealth.healthmanagermain.service.domain.UserAccountService;
import com.myhealth.healthmanagermain.service.dto.UserAccountDTO;
import com.myhealth.healthmanagermain.web.rest.util.PaginationUtil;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

  private final UserAccountService userAccountService;

  @PostMapping("/users")
  @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
  public ResponseEntity<Void> createUser(@Valid @RequestBody UserAccountDTO userAccountDTO)
      throws URISyntaxException {
    log.debug("REST request to save User : {}", userAccountDTO);

    userAccountService.createUser(userAccountDTO);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/users")
  public ResponseEntity<List<UserAccountDTO>> getAllUsers(Pageable pageable) {
    final Page<UserAccountDTO> page = userAccountService.getAllManagedUsers(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users");
    return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
  }

  @GetMapping("/users/authorities")
  @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
  public List<String> getAuthorities() {
    return userAccountService.getAuthorities();
  }

  @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
  public ResponseEntity getUser(@PathVariable String login) {
    log.debug("REST request to get User : {}", login);
    Optional<UserAccount> userOptional = userAccountService.getUserWithAuthoritiesByLogin(login);
    if (userOptional.isPresent()) {
      return ResponseEntity.ok().body(userOptional.map(UserAccountDTO::new).get());
    }
    return new ResponseEntity(HttpStatus.NOT_FOUND);
  }

  @DeleteMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
  @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
  public ResponseEntity<Void> deleteUser(@PathVariable String login) {
    log.debug("REST request to delete User: {}", login);
    userAccountService.deleteUser(login);
    return ResponseEntity.ok().build();
  }
}
