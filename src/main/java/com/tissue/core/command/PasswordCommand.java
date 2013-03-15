package com.tissue.core.command;

import com.tissue.core.Account;

public interface PasswordCommand {
    String getPassword();
    Account getAccount();
}
