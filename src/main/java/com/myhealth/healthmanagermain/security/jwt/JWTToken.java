package com.myhealth.healthmanagermain.security.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class JWTToken {

  @JsonProperty("id_token")
  private String idToken;

}
