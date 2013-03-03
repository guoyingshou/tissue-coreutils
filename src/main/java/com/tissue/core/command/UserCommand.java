package com.tissue.core.command;

import com.tissue.core.social.User;
import com.tissue.core.social.Account;

public interface UserCommand {

    String getId();
    String getUsername();
    String getPassword();
    String getEmail();

    String getDisplayName();
    String getHeadline();

    /**
    User getUser();
    Account getAccount();
    */
}
