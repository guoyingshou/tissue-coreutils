package com.tissue.core.command;

import com.tissue.core.Account;

public interface PostCommand {
    String getId();
    String getTitle();
    String getContent();
    String getType();
    Account getAccount();
}
