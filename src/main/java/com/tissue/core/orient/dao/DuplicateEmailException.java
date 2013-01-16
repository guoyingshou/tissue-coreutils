package com.tissue.core.orient.dao;

public class DuplicateEmailException extends Exception {
    public DuplicateEmailException(String message, Throwable e) {
        super(message, e);
    }
}
