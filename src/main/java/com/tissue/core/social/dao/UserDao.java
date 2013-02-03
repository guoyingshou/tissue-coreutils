package com.tissue.core.social.dao;

import com.tissue.core.social.User;
import com.tissue.core.social.Impression;
import com.tissue.core.social.Invitation;
import java.util.List;

public interface UserDao {

    User create(User user);

    User update(User user);

    void changePassword(User user);

    boolean isUsernameExist(String username);

    boolean isEmailExist(String email);

    void addResume(String userId, String content);

    void inviteFriend(String fromId, String toId, String content);

    List<Invitation> getInvitationsReceived(String userId);

    List<Invitation> getInvitationsSent(String userId);

    void acceptInvitation(String invitationId);

    void declineInvitation(String invitationId);

    void addImpression(Impression impression);

    List<Impression> getImpressions(String userId);

    User getUserById(String userId);

    List<User> getFriends(String userId);

    boolean isInvitable(String userId1, String userId2);

    List<User> getNewUsers(String excludingUserId, int limit);


}
