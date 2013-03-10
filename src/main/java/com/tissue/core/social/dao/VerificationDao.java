package com.tissue.core.social.dao;


import com.tissue.core.command.VerificationCommand;

public interface VerificationDao {

    /**
     * @userId user id
     * @return uuid verification code
     */
    String create(VerificationCommand command);

    String getAccountId(String code);

    void setVerified(String accountId);
}
