package com.tissue.social.dao.orient;

import com.tissue.core.util.OrientDataSource;
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

    public List<Activity> getActivities(int num) {
        String sql = "select from EdgeAction where label in ['friend', 'topic', 'plan', 'member', 'concept', 'note', 'tutorial', 'question'] order by createTime desc limit " + num;
        logger.debug(sql);

        List<Activity> activities = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            ActivityStreamMapper mapper = new ActivityStreamMapper();
            activities = mapper.process(docs);
        }
        finally {
            db.close();
        }
        return activities;
    }

    public List<Activity> getWatchedActivities(String accountId, int num) {
        List<Activity> activities = new ArrayList();

        String sql = "select from EdgeAction where out.user in (select set(user.in[label='friend'].out, user.out[label='friend'].in) from " + accountId + ") or (" + accountId + " in set(in.plan.in.out, in.article.plan.in.out, in.message.article.plan.in.out, in.question.plan.in.out, in.answer.question.plan.in.out) and out not in " + accountId + ") order by createTime desc limit " + num;
        logger.debug(sql);

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            ActivityStreamMapper mapper = new ActivityStreamMapper();
            activities = mapper.process(docs);
        }
        finally {
            db.close();
        }
        return activities;
    }

    public List<Activity> getActivitiesByAccount(String accountId, int num) {
        String sql = "select from EdgeAction where out in " + accountId + " order by createTime desc";
        logger.debug(sql);

        List<Activity> activities = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            ActivityStreamMapper mapper = new ActivityStreamMapper();
            activities = mapper.process(docs);
        }
        finally {
            db.close();
        }
        return activities;
    }

    public List<Activity> getActivitiesByUser(String userId, int num) {
        String sql = "select from EdgeAction where out.user in " + userId + " order by createTime desc";
        logger.debug(sql);

        List<Activity> activities = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            ActivityStreamMapper mapper = new ActivityStreamMapper();
            activities = mapper.process(docs);
        }
        finally {
            db.close();
        }
        return activities;
    }

    public List<Activity> getActivitiesForNewUser(int num) {
        String sql = "select from EdgeAction where label in ['topic', 'plan', 'member', 'concept', 'note', 'tutorial', 'question'] order by createTime desc limit " + num;
        logger.debug(sql);

        List<Activity> activities = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            ActivityStreamMapper mapper = new ActivityStreamMapper();
            activities = mapper.process(docs);
        }
        finally {
            db.close();
        }
        return activities;
    }

}
