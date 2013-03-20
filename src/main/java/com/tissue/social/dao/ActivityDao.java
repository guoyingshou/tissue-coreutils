package com.tissue.social.dao;

import com.tissue.social.Activity;
import java.util.List;

public interface ActivityDao {

    List<Activity> getSelfActivities(String userId, int num);

    List<Activity> getWatchedActivities(String userId, int num);

    List<Activity> getActivities(int num);

    List<Activity> getActivitiesForNewUser(int num);

}
