package com.tissue.core.command;

import com.tissue.core.Account;

public interface CommentCommand {
    String getId();
    String getContent();
    Account getAccount();
}
