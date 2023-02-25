package com.myhealth.healthmanagermain.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.NonNull;

@Schema(description = "Contains a user list of personal records")
public record PersonalRecordsDTO(
    @NonNull @JsonProperty("personalRecords") List<PersonalRecordDTO> personalRecords) {

}
