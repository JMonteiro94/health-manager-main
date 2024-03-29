package com.myhealth.healthmanagermain.web.rest.errors;

import java.io.Serial;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class BadRequestApiException extends AbstractThrowableProblem {

  @Serial
  private static final long serialVersionUID = 1L;

  private final String entityName;

  private final String errorKey;

  public BadRequestApiException(String defaultMessage, String entityName, String errorKey) {
    this(ErrorConstants.DEFAULT_TYPE, defaultMessage, entityName, errorKey);
  }

  public BadRequestApiException(URI type, String defaultMessage, String entityName,
      String errorKey) {
    super(type, defaultMessage, Status.BAD_REQUEST, null, null, null,
        getAlertParameters(entityName, errorKey));
    this.entityName = entityName;
    this.errorKey = errorKey;
  }

  public String getEntityName() {
    return entityName;
  }

  public String getErrorKey() {
    return errorKey;
  }

  private static Map<String, Object> getAlertParameters(String entityName, String errorKey) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("message", "error." + errorKey);
    parameters.put("params", entityName);
    return parameters;
  }

}
