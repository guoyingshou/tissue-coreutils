package com.tissue.core.social.dao;

import com.tissue.core.social.Activity;
import com.tissue.core.social.ActivityObject;
import java.util.List;

public interface ActivityDao {
    List<Activity> getFriendsActivities(String userId, int num);
    List<Activity> getUserActivities(String userId, int num);
    List<Activity> getActivitiesForNewUser(int num);
    List<Activity> getActivities(int num);
}
