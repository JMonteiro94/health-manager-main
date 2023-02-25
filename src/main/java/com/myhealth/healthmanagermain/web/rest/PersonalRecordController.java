package com.myhealth.healthmanagermain.web.rest;

import com.myhealth.healthmanagermain.security.AuthoritiesConstants;
import com.myhealth.healthmanagermain.security.SecurityUtils;
import com.myhealth.healthmanagermain.service.PersonalRecordManagementService;
import com.myhealth.healthmanagermain.web.rest.dto.PersonalRecordDTO;
import com.myhealth.healthmanagermain.web.rest.dto.PersonalRecordsDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class PersonalRecordController {

  @NonNull
  private final PersonalRecordManagementService personalRecordManagementService;

  @GetMapping("/personal-record")
  @PreAuthorize(
      "hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "','" + AuthoritiesConstants.USER + "')")
  public ResponseEntity<PersonalRecordsDTO> getUserPersonalRecords() {
    String username = SecurityUtils.getCurrentUsernameOrThrowException();
    List<PersonalRecordDTO> personalRecords = personalRecordManagementService.getAllUserPersonalRecordsByUsername(
        username);
    return ResponseEntity.ok(new PersonalRecordsDTO(personalRecords));
  }
}
