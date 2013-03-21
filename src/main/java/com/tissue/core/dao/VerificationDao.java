package com.tissue.core.dao;


import com.tissue.core.Verification;
import com.tissue.core.command.VerificationCommand;

public interface VerificationDao {

    String create(VerificationCommand command);

    Verification getVerification(String code);

    void delete(String verificationId);

}
