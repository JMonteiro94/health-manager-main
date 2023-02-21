package com.myhealth.healthmanagermain.web.rest.errors;

import java.io.Serial;
import java.net.URI;

public class EmailAlreadyUsedException extends BadRequestApiException {

  @Serial
  private static final long serialVersionUID = 1L;

  public EmailAlreadyUsedException() {
    super(URI.create("localhost:8080/email-already-in-use"), "Email is already in use!",
        "userManagement",
        "emailexists");
  }
}
