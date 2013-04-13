package com.tissue.social.dao;

import com.tissue.social.Activity;
import java.util.List;

public interface ActivityDao {

    List<Activity> getActivitiesByAccount(String accountId, int num);

    List<Activity> getActivitiesByUser(String userId, int num);

    List<Activity> getWatchedActivities(String accountId, int num);

    List<Activity> getActivities(int num);

    List<Activity> getActivitiesForNewUser(int num);

}
