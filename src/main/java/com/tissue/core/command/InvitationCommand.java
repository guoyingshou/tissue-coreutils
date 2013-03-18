package com.tissue.core.command;

import com.tissue.core.User;

public interface InvitationCommand extends ContentCommand {
    User getTo();
}
