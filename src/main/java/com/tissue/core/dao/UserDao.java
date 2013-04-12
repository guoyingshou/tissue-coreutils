package com.tissue.core.dao;

import com.tissue.core.Account;
import com.tissue.core.User;
import com.tissue.core.command.UserCommand;
import java.util.List;

public interface UserDao {

    User getUser(String userId);

    User getUserByAccount(String accountId);

    List<User> getNewUsers(String excludingAccountId, int limit);

    List<User> getFriends(String userId);

    Boolean isFriend(String userId1, String userId2);

    void removeRelation(String userId1, String userId2);

    void updateHeadline(UserCommand command);
}
