package com.tissue.social.dao;

import com.tissue.core.Account;
import com.tissue.core.User;
import com.tissue.social.command.InvitationCommand;
import com.tissue.social.Invitation;
import java.util.List;

public interface InvitationDao {

    String create(InvitationCommand command);

    Invitation getInvitation(String invitationId);

    List<Invitation> getInvitationsReceived(String userId);

    void declineInvitation(Invitation invitation);

    void acceptInvitation(Invitation invitation);

    Boolean isInvitable(User owner, Account viewerAccount);
}
