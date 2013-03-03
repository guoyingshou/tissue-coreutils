package com.tissue.core.command;

import com.tissue.core.social.Account;

public interface InvitationCommand {
    String getId();
    String getLetter();
    String getUserId();
    Account getAccount();
}
