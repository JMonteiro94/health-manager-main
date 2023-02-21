package com.myhealth.healthmanagermain.web.rest.errors;

import java.io.Serial;
import java.net.URI;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public final class InvalidPasswordException extends AbstractThrowableProblem {

  @Serial
  private static final long serialVersionUID = 1L;

  public InvalidPasswordException() {
    super(URI.create("localhost:8080/invalid-password"), "Incorrect password", Status.BAD_REQUEST);
  }
}
