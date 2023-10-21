package com.example.aquatrack_backend.exception;

public class UserUnauthorizedException extends Exception{
  public UserUnauthorizedException(String message) {
    super(message);
  }
}
