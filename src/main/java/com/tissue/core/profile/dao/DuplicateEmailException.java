package com.tissue.core.profile.dao;

public class DuplicateEmailException extends Exception {
    public DuplicateEmailException(String message, Throwable e) {
        super(message, e);
    }
}
