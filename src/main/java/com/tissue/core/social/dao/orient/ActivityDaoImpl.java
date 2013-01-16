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
/**
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;
*/

//import com.orientechnologies.orient.core.command.traverse.OTraverse;
//import com.orientechnologies.orient.core.command.OCommandPredicate;
//import com.orientechnologies.orient.core.command.OCommandContext;
//import com.orientechnologies.orient.core.record.ORecord;

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
        String ridUser = OrientIdentityUtil.decode(userId);
        String sql = "select from edgeactivity where out in (select union(in[status='accepted'].out, out[status='accepted'].in) from " + ridUser + ") order by createTime desc limit " + num;

        OGraphDatabase db = dataSource.getDB();
        try {
        List<ODocument> docs = query(db, sql);
        return ActivityMapper.buildActivities(docs);
        }
        finally {
            db.close();
        }
    }

    public List<Activity> getUserActivities(String userId, int num) {

        String rid = OrientIdentityUtil.decode(userId);
        String sql = "select from edgeactivity where out in " + rid + " order by createTime desc";

        OGraphDatabase db = dataSource.getDB();
        try {
        List<ODocument> docs = query(db, sql);
        return ActivityMapper.buildActivities(docs);
        }
        finally {
            db.close();
        }
 
        //return query(sql);
    }

    public List<Activity> getActivitiesForNewUser(int num) {
        String sql = "select from edgetopic where label contains ['create', 'plan', 'members', 'concept', 'note', 'tutorial', 'question'] order by createTime desc limit " + num;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            return ActivityMapper.buildActivities(docs);
        }
        finally {
            db.close();
        }
 
    }

    public List<Activity> getActivities(int num) {
        String sql = "select from edgetopic order by createTime desc limit " + num;

        OGraphDatabase db = dataSource.getDB();
        try {
        List<ODocument> docs = query(db, sql);
        return ActivityMapper.buildActivities(docs);
        }
        finally {
            db.close();
        }
 
        //return query(sql);
    }

    /**
    private List<Activity> query(String sql) {
        List<Activity> stream = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            OSQLSynchQuery<ODocument> q = new OSQLSynchQuery(sql);
            List<ODocument> streamDoc = db.query(q);
            stream = ActivityMapper.buildStream(streamDoc);
        }
        catch(Exception exc) {
            //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return stream;
    }
    */

}
