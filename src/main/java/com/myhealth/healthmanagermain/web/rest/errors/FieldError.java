package com.myhealth.healthmanagermain.web.rest.errors;

import java.io.Serializable;

public record FieldError(String objectName, String field, String message) implements Serializable {

}
