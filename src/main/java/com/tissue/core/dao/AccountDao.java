package com.tissue.core.dao;

import com.tissue.core.Account;
import com.tissue.core.command.UserCommand;
import com.tissue.core.command.EmailCommand;
import com.tissue.core.command.PasswordCommand;
//import com.tissue.core.command.ProfileCommand;
import java.util.List;

public interface AccountDao {

    boolean isUsernameExist(String username);

    boolean isEmailExist(String email);

    //boolean isEmailExist(String excludingUserId, String email);

    String create(UserCommand userCommand);

    void updateEmail(EmailCommand command);

    void updatePassword(PasswordCommand command);

    Account getAccount(String accountId);

    /**
    void updateProfile(ProfileCommand ProfileCommand);
    
    User getUser(String userId);

    User getUserByAccount(String accountId);

    String getUserIdByAccount(String accountId);


    List<User> getNewUsers(String excludingUserId, int limit);

    List<User> getFriends(String userId);

    Boolean isFriend(String userId1, String userId2);
    */

}
