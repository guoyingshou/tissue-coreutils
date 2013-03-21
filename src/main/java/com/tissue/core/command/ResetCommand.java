package com.tissue.core.command;

import com.tissue.core.Account;

public interface ResetCommand {
    String getId();
    String getCode();
    Account getAccount();
}
