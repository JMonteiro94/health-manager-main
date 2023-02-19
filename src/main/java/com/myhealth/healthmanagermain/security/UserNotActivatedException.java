package com.myhealth.healthmanagermain.security;

public class UserNotActivatedException extends RuntimeException {

  public UserNotActivatedException(String message) {
    super(message);
  }
}
