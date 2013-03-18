package com.tissue.core.command;

public interface UserCommand {

    String getId();
    String getUsername();
    String getPassword();
    String getEmail();

    String getDisplayName();
    String getHeadline();

}
