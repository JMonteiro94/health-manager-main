package com.myhealth.healthmanagermain.web.rest.errors;

import java.io.Serial;
import java.net.URI;

public class UsernameAlreadyUsedException extends BadRequestApiException {

  @Serial
  private static final long serialVersionUID = 1L;

  public UsernameAlreadyUsedException() {
    super(URI.create("localhost:8080/username-already-used"), "Login name already used!",
        "userManagement",
        "userexists");
  }
}
