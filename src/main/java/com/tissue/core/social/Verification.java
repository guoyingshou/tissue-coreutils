package com.tissue.core.social;

import java.io.Serializable;

public class Verification implements Serializable {
    private String id;
    private String code;
    private String accountId;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }
}
