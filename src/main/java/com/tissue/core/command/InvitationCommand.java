package com.tissue.core.command;

import com.tissue.core.User;
import com.tissue.core.Account;

public interface InvitationCommand {
    String getId();
    String getContent();
    User getTo();
    Account getFrom();
}
