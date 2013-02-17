package com.tissue.core.command;

import com.tissue.core.social.User;

public interface ItemCommand {
    String getId();
    String getContent();
    User getUser();
}
