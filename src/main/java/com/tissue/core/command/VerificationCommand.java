package com.tissue.core.command;

import com.tissue.core.Account;

public interface VerificationCommand {
    String getCode();
    Account getAccount();
}
