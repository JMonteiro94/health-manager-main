package com.myhealth.healthmanagermain.web.rest.errors;

import java.io.Serial;
import java.net.URI;

public class LoginAlreadyUsedException extends BadRequestApiException {

  @Serial
  private static final long serialVersionUID = 1L;

  public LoginAlreadyUsedException() {
    super(URI.create("localhost:8080/username-already-in-use"), "Login name already used!",
        "userManagement",
        "userexists");
  }
}
