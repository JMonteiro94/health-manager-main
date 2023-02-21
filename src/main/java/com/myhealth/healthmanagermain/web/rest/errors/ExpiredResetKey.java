package com.myhealth.healthmanagermain.web.rest.errors;

import java.io.Serial;
import java.net.URI;

public final class ExpiredResetKey extends BadRequestApiException {

  @Serial
  private static final long serialVersionUID = 1L;

  public ExpiredResetKey() {
    super(URI.create("localhost:8080/expired-reset-key"), "Reset key expired",
        "userManagement",
        "keyexpered");
  }
}
