package com.tissue.core.command;

import com.tissue.core.social.Account;

public interface PasswordCommand {
    String getPassword();
    Account getAccount();
}
