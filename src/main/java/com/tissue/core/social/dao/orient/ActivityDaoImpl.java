package com.tissue.core.social.dao.orient;

import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.mapper.ActivityMapper;
import com.tissue.core.util.OrientIdentityUtil;
//import com.tissue.core.util.OrientDataSource;
import com.tissue.core.social.Activity;
import com.tissue.core.social.ActivityObject;
import com.tissue.core.social.User;
import com.tissue.core.social.dao.ActivityDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

import com.google.common.collect.Multimap;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

@Component
public class ActivityDaoImpl extends OrientDao implements ActivityDao {

    public List<Activity> getFriendsActivities(String userId, int num) {
        List<Activity> activities = new ArrayList();

        String ridUser = OrientIdentityUtil.decode(userId);
        String sql = "select from ographedge where out in (select union(in[label='friends'].out, out[label='friends'].in) from " + ridUser + ") and (label contains ['concept', 'topic']) order by createTime desc limit " + num;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            for(ODocument doc : docs) {
                Activity activity = ActivityMapper.buildActivity(doc);
                activities.add(activity);
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return activities;
    }

    public List<Activity> getUserActivities(String userId, int num) {
        List<Activity> activities = new ArrayList();

        String rid = OrientIdentityUtil.decode(userId);
        String sql = "select from ographedge where out in " + rid + " order by createTime desc";

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            for(ODocument doc : docs) {
                Activity act = ActivityMapper.buildActivity(doc);
                activities.add(act);
            }
            //activities = ActivityMapper.buildActivities(docs);
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
         finally {
            db.close();
        }
        return activities;
    }

    public List<Activity> getActivitiesForNewUser(int num) {
        List<Activity> activities = new ArrayList();

        String sql = "select from ographedge where label contains ['topic', 'plan', 'members', 'concept', 'note', 'tutorial', 'question'] order by createTime desc limit " + num;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            for(ODocument doc : docs) {
                Activity act = ActivityMapper.buildActivity(doc);
                activities.add(act);
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return activities;
    }

    public List<Activity> getActivities(int num) {
        List<Activity> activities = new ArrayList();

        String sql = "select from ographedge order by createTime desc limit " + num;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            for(ODocument doc : docs) {
                Activity act = ActivityMapper.buildActivity(doc);
                activities.add(act);
            }
            //activities = ActivityMapper.buildActivities(docs);
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return activities;
    }

}
