package com.tissue.core.command;

/**
import com.tissue.core.User;
import com.tissue.core.Account;
*/

public interface UserCommand {

    String getId();
    String getUsername();
    String getPassword();
    String getEmail();

    String getDisplayName();
    String getHeadline();

}
