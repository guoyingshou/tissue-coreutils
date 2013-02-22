package com.tissue.core.command;

import com.tissue.core.social.User;

public interface EmailCommand {
    String getEmail();
    User getUser();
}
