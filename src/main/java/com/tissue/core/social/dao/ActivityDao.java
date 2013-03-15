package com.tissue.core.social.dao;

import com.tissue.core.Account;
import com.tissue.core.User;
import com.tissue.core.About;
import com.tissue.core.command.UserCommand;
import com.tissue.core.command.ProfileCommand;
import com.tissue.core.command.EmailCommand;
import com.tissue.core.command.PasswordCommand;
import com.tissue.core.command.InvitationCommand;
import com.tissue.core.command.ImpressionCommand;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Post;
import com.tissue.core.social.Impression;
import com.tissue.core.social.Invitation;
import com.tissue.core.social.Activity;
import java.util.List;

public interface ActivityDao {

    List<Activity> getSelfActivities(String userId, int num);

    List<Activity> getWatchedActivities(String userId, int num);

    List<Activity> getActivities(int num);

    List<Activity> getActivitiesForNewUser(int num);

}
