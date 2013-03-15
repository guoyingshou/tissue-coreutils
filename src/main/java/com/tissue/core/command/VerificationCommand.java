package com.tissue.core.command;

import com.tissue.core.Account;

public interface VerificationCommand {
    String getCode();
    String getEmail();
    Account getAccount();
}
