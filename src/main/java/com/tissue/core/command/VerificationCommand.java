package com.tissue.core.command;

import com.tissue.core.social.Account;

public interface VerificationCommand {
    String getCode();
    String getEmail();
    Account getAccount();
}
