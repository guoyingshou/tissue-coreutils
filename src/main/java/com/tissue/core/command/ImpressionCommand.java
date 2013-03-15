package com.tissue.core.command;

import com.tissue.core.User;
import com.tissue.core.Account;

public interface ImpressionCommand {
    String getContent();
    User getTo();
    Account getAccount();
}
