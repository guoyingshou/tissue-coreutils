package com.tissue.social.command;

import com.tissue.core.command.ContentCommand;
import com.tissue.core.User;

public interface InvitationCommand extends ContentCommand {
    User getTo();
}
