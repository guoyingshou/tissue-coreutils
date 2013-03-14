package com.tissue.core.command;

import com.tissue.core.social.User;
import com.tissue.core.social.Account;

public interface ImpressionCommand {
    String getContent();
    User getTo();
    Account getAccount();
}
