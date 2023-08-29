package com.example.aquatrack_backend.exception;

public class FailedToAuthenticateUserException extends Exception {
  public FailedToAuthenticateUserException(String message) {
    super(message);
  }
}
