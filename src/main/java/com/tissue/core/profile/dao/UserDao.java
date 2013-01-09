package com.tissue.core.profile.dao;

import com.tissue.core.profile.User;
import java.util.List;

public interface UserDao {
    User create(User user);
    User update(User user);
    User getUserByEmail(String email);
    User getUserById(String id);

    boolean isFriend(String userId1, String userId2);

    List<User> getFriends(String viewerId);

    //void getEdges(String fromId, String toId);
}
