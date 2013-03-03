package com.tissue.core.command;

import com.tissue.core.social.Account;

public interface AboutCommand {
    String getContent();
    Account getAccount();
}
