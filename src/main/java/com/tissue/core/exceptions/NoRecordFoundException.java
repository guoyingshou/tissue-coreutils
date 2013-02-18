package com.tissue.core.exceptions;

public class NoRecordFoundException extends RuntimeException {
    private String rid;

    public NoRecordFoundException(String rid) {
        this.rid = rid;
    }

    public String getMessage() {
        return rid + " not exist";
    }
}
