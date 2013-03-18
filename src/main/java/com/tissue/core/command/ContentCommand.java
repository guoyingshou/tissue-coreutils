package com.tissue.core.command;

import com.tissue.core.Account;

public interface ContentCommand {
    String getId();
    String getContent();
    Account getAccount();
}
