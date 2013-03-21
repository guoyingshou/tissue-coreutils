package com.tissue.core.command;

import com.tissue.core.Account;

public interface UserCommand {

    String getId();
    String getDisplayName();
    String getHeadline();

    Account getAccount();

}
