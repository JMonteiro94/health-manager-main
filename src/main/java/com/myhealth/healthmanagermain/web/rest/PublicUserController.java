package com.myhealth.healthmanagermain.web.rest;


import com.myhealth.healthmanagermain.service.domain.AuthorityService;
import com.myhealth.healthmanagermain.service.domain.UserAccountService;
import com.myhealth.healthmanagermain.service.dto.UserAccountDTO;
import com.myhealth.healthmanagermain.web.rest.util.PaginationUtil;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class PublicUserController {

  private static final List<String> ALLOWED_ORDERED_PROPERTIES = List.of("username", "email",
      "langKey");

  @NonNull
  private final UserAccountService userService;
  @NonNull
  private final AuthorityService authorityService;

  @GetMapping("/users")
  public ResponseEntity<List<UserAccountDTO>> getUsersPublicInformation(
      @ParameterObject Pageable pageable) {
    log.debug("REST request to get all public UserAccount names");
    if (!onlyContainsAllowedProperties(pageable)) {
      return ResponseEntity.badRequest().build();
    }

    final Page<UserAccountDTO> page = userService.getAllPublicUsers(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
        ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
  }

  private boolean onlyContainsAllowedProperties(Pageable pageable) {
    return pageable.getSort().stream().map(Sort.Order::getProperty)
        .allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
  }

  @GetMapping("/authorities")
  public Set<String> getAuthorities() {
    return authorityService.getAll();
  }
}