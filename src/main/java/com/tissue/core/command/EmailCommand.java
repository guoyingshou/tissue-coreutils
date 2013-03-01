package com.tissue.core.command;

import com.tissue.core.social.Account;

public interface EmailCommand {
    String getEmail();
    Account getAccount();
}
