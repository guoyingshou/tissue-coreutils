package com.tissue.core.social.dao;

import com.tissue.core.command.UserCommand;
import com.tissue.core.command.ProfileCommand;
import com.tissue.core.command.EmailCommand;
import com.tissue.core.command.PasswordCommand;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Post;
import com.tissue.core.social.Account;
import com.tissue.core.social.User;
import com.tissue.core.social.Impression;
import com.tissue.core.social.Invitation;
import java.util.List;

public interface UserDao {

    String create(UserCommand userCommand);

    void updateProfile(ProfileCommand ProfileCommand);

    void updateEmail(EmailCommand command);

    void updatePassword(PasswordCommand command);

    Account getUserAccount(String accountId);

    boolean isUsernameExist(String username);

    boolean isEmailExist(String email);

    boolean isEmailExist(String excludingUserId, String email);

    void addResume(String userId, String content);

    void inviteFriend(String fromId, String toId, String content);

    List<Invitation> getInvitationsReceived(String userId);

    List<Invitation> getInvitationsSent(String userId);

    void acceptInvitation(String invitationId);

    void declineInvitation(String invitationId);

    void addImpression(Impression impression);

    List<Impression> getImpressions(String userId);

    List<User> getFriends(String userId);

    boolean isInvitable(String userId1, String userId2);

    List<User> getNewUsers(String excludingUserId, int limit);

    /**
     * topic
     */
    List<Topic> getNewTopics(String excludingUserId, int limit);

    /**
     * plan
     */
    List<Plan> getPlans(String userId);

    /**
     * post
     */
    long getPostsCount(String userId);

    List<Post> getPagedPosts(String userId, int page, int size);

}
