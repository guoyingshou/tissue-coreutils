package com.tissue.core.social.dao;

import com.tissue.core.social.Invitation;
import java.util.List;

public interface InvitationDao {

    void inviteFriend(String fromId, String toId, String content);

    void declineInvitation(String invitationId);

    void acceptInvitation(String invitationId);

}
