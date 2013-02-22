package com.tissue.core.command;

import com.tissue.core.social.Account;

public interface ItemCommand {
    String getId();
    String getContent();
    Account getAccount();
}
