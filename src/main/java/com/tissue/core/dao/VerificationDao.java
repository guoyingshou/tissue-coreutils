package com.tissue.core.dao;


import com.tissue.core.command.VerificationCommand;

public interface VerificationDao {

    String create(VerificationCommand command);

    String getAccountId(String code);

    void setVerified(String accountId);
}
