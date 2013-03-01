package com.tissue.core.command;

import com.tissue.core.social.Account;

public interface ProfileCommand {

    String getDisplayName();
    String getHeadline();

    Account getAccount();
}
