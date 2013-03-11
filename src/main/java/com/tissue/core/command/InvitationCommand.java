package com.tissue.core.command;

import com.tissue.core.social.User;
import com.tissue.core.social.Account;

public interface InvitationCommand {
    String getId();
    String getContent();
    User getTo();
    Account getFrom();
}
