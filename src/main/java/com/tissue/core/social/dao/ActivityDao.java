package com.tissue.core.social.dao;

import com.tissue.core.social.Activity;
import com.tissue.core.social.ActivityObject;
import java.util.List;

public interface ActivityDao {
    List<Activity> getTopicRelatedActivities(String userId, int num);
    List<Activity> getFriendsActivities(String userId, int num);
    List<Activity> getLatestActivities(int num);
}
