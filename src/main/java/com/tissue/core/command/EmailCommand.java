package com.tissue.core.command;

import com.tissue.core.Account;

public interface EmailCommand {
    String getEmail();
    Account getAccount();
}
