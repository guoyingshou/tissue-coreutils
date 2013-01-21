package com.tissue.core.social.dao;

import com.tissue.core.social.User;
import com.tissue.core.social.Impression;
import java.util.List;

public interface UserDao {

    User create(User user);

    User update(User user);

    void addResume(String userId, String content);

    void inviteFriend(String fromId, String toId, String content);

    void acceptInvitation(String id);

    void declineInvitation(String id);

    void addImpression(Impression impression);

    List<Impression> getImpressions(String userId);

    User getUserByEmail(String email);

    User getUserById(String id);
    
    User getUserDetailsById(String id);

}
