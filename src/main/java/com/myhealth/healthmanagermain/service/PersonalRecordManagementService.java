package com.myhealth.healthmanagermain.service;

import com.myhealth.healthmanagermain.web.rest.dto.PersonalRecordDTO;
import java.util.List;

public interface PersonalRecordManagementService {

  List<PersonalRecordDTO> getAllUserPersonalRecordsByUsername(String username);

}
