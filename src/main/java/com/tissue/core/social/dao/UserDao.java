package com.tissue.core.social.dao;

import com.tissue.core.social.User;
import com.tissue.core.social.Impression;
import java.util.List;

public interface UserDao {

    User create(User user);

    User update(User user);

    void addResume(String userId, String content);

    void addImpression(Impression impression);

    List<Impression> getImpressions(String userId);

    User getUserByEmail(String email);

    User getUserById(String id, boolean withFriends);


    boolean isFriend(String userId1, String userId2);

    List<User> getFriends(String viewerId);

}
