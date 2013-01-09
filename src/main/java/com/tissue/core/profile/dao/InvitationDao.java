package com.tissue.core.profile.dao;

import com.tissue.core.profile.Invitation;
import java.util.List;

public interface InvitationDao {

    boolean canInvite(String userId1, String userId2);

    void inviteFriend(String fromId, String toId, String content);

    Invitation getInvitation(String invitationid);

    List<Invitation> getInvitations(String userId);

    boolean declineInvitation(String invitationId);

    Invitation acceptInvitation(String invitationId);

}
