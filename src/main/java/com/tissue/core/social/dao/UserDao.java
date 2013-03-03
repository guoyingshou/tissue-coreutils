package com.tissue.core.social.dao;

import com.tissue.core.command.UserCommand;
import com.tissue.core.command.ProfileCommand;
import com.tissue.core.command.EmailCommand;
import com.tissue.core.command.PasswordCommand;
import com.tissue.core.command.InvitationCommand;
import com.tissue.core.command.ImpressionCommand;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Post;
import com.tissue.core.social.Account;
import com.tissue.core.social.User;
import com.tissue.core.social.Impression;
import com.tissue.core.social.Invitation;
import com.tissue.core.social.Activity;
import com.tissue.core.social.About;
import java.util.List;

public interface UserDao {

    boolean isUsernameExist(String username);

    boolean isEmailExist(String email);

    boolean isEmailExist(String excludingUserId, String email);

    String create(UserCommand userCommand);

    //void addResume(String userId, String content);

    void updateProfile(ProfileCommand ProfileCommand);

    void updateEmail(EmailCommand command);

    void updatePassword(PasswordCommand command);

    User getUser(String userId);

    User getUserByAccount(String accountId);

    String getUserIdByAccount(String accountId);

    Account getAccount(String accountId);

    List<User> getNewUsers(String excludingUserId, int limit);

    /**
     * friends
     */
    List<User> getFriends(String userId);

    Boolean isFriend(String userId1, String userId2);

    /**
     * activities
     */
    List<Activity> getActivities(String userId, int num);
 
    /**
     * topic
     */
    List<Topic> getNewTopics(String excludingUserId, int limit);

    /**
     * plan
     */
    List<Plan> getPlans(String userId);

    List<Plan> getPlansByAccount(String accountId);

    /**
     * post
     */
    long getPostsCount(String userId);

    List<Post> getPagedPosts(String userId, int page, int size);

}
