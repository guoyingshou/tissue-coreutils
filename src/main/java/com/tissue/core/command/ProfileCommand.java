package com.tissue.core.command;

import com.tissue.core.social.User;

public interface ProfileCommand {
    String getDisplayName();
    String getHeadline();

    User getUser();
}
