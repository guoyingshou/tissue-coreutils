package com.tissue.social.dao.orient;

import com.tissue.core.datasources.OrientDataSource;
import com.tissue.social.mapper.ActivityStreamMapper;
import com.tissue.social.Activity;
import com.tissue.social.dao.ActivityDao;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.nio.charset.Charset;
import com.google.common.hash.Hashing;

@Repository
public class ActivityDaoImpl implements ActivityDao {

    private static Logger logger = LoggerFactory.getLogger(ActivityDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    /**
     * Latest activities intended to by used by users who had signed in.
     */
    public List<Activity> getActivities(String accountId, int num) {
        String sql = "select out.user as user, in as what, category, createTime from EdgeAction" +
                     " where in.deleted is null" +
                     " and out not in " + accountId +
                     " and category in ['topic', 'plan', 'member', 'concept', 'note', 'tutorial', 'question', 'answer']" +
                     " order by createTime desc" +
                     " limit " + num;
        logger.debug(sql);

        List<Activity> activities = new ArrayList();

        OrientGraph db = dataSource.getDB();
        try {
            List<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            ActivityStreamMapper mapper = new ActivityStreamMapper();
            activities = mapper.process(docs);
        }
        finally {
            db.shutdown();
        }
        return activities;
    }

    public List<Activity> getWatchedActivities(String accountId, int num) {
        List<Activity> activities = new ArrayList();

        String sql = "select out.user as user, in as what, category, createTime from EdgeAction" + 
                     " let $plans = (select from plan where in_.out in " + accountId + ")" +
                     " where in.deleted is null " +
                     //except for myself
                     " and out not in " + accountId + 
                     //friends'activities
                     " and (out.user in (select set(user.in_[category='friend'].out, user.out_[category='friend'].in) from " + accountId + ")" + 
                     //activities in the groups joined
                     " or in.plan in $plans" + 
                     " or in.article.plan in $plans" + 
                     " or in.message.article.plan in $plans" + 
                     " or in.question.plan in $plans" + 
                     " or in.answer.question.plan in $plans" + 
                     ")" + 
                     " order by createTime desc limit " + num;


        logger.debug(sql);

        OrientGraph db = dataSource.getDB();
        try {
            List<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();

            ActivityStreamMapper mapper = new ActivityStreamMapper();
            activities = mapper.process(docs);
        }
        finally {
            db.shutdown();
        }
        return activities;
    }

    public List<Activity> getActivitiesByUser(String userId, int num) {
        String sql = "select out.user as user, in as what, category, createTime from EdgeAction" +
                     " where in.deleted is null" +
                     " and out.user in " + userId +
                     " order by createTime desc" +
                     " limit " + num;
        logger.debug(sql);

        List<Activity> activities = new ArrayList();

        OrientGraph db = dataSource.getDB();
        try {
            List<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();

            ActivityStreamMapper mapper = new ActivityStreamMapper();
            activities = mapper.process(docs);
        }
        finally {
            db.shutdown();
        }
        return activities;
    }

    /**
     * Activities to be presented to anonymous users.
     */
    public List<Activity> getActivitiesForNewUser(int num) {
        String sql = "select out.user as user, in as what, category, createTime from EdgeAction" +
                     " where in.deleted is null" + 
                     " and category in ['topic', 'plan', 'member', 'concept', 'note', 'tutorial', 'question', 'answer']" +
                     " order by createTime desc" +
                     " limit " + num;
        logger.debug(sql);

        List<Activity> activities = new ArrayList();

        OrientGraph db = dataSource.getDB();
        try {
            List<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();

            ActivityStreamMapper mapper = new ActivityStreamMapper();
            activities = mapper.process(docs);
        }
        finally {
            db.shutdown();
        }
        return activities;
    }

}
