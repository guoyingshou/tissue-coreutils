package com.tissue.core.dao;

import com.tissue.core.Account;
import com.tissue.core.User;
import com.tissue.core.command.UserCommand;
import com.tissue.core.command.ProfileCommand;
import com.tissue.core.command.EmailCommand;
import com.tissue.core.command.PasswordCommand;
import java.util.List;

public interface UserDao {

    User getUser(String userId);

    User getUserByAccount(String accountId);

    String getUserIdByAccount(String accountId);

    List<User> getNewUsers(String excludingUserId, int limit);

    List<User> getFriends(String userId);

    Boolean isFriend(String userId1, String userId2);

    void updateProfile(ProfileCommand ProfileCommand);
}
