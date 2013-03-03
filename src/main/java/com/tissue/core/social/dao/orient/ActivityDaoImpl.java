package com.tissue.core.social.dao.orient;

import com.tissue.core.command.UserCommand;
import com.tissue.core.command.ProfileCommand;
import com.tissue.core.command.EmailCommand;
import com.tissue.core.command.PasswordCommand;
import com.tissue.core.exceptions.NoRecordFoundException;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.mapper.TopicMapper;
import com.tissue.core.mapper.PlanMapper;
import com.tissue.core.mapper.PostMapper;
import com.tissue.core.mapper.UserMapper;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.core.mapper.ActivityStreamMapper;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Post;
import com.tissue.core.social.Account;
import com.tissue.core.social.User;
import com.tissue.core.social.Activity;
import com.tissue.core.social.About;
import com.tissue.core.social.dao.ActivityDao;

import org.springframework.stereotype.Component;
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

@Component
public class ActivityDaoImpl implements ActivityDao {

    private static Logger logger = LoggerFactory.getLogger(ActivityDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public List<Activity> getActivities(int num) {
        String sql = "select from EdgeAction where label in ['friend', 'topic', 'hostGroup', 'joinGroup', 'concept', 'note', 'tutorial', 'question'] order by createTime desc limit " + num;
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

    public List<Activity> getActivities(String userId, int num) {
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
        String sql = "select from EdgeAction where label in ['topic', 'hostGroup', 'joinGroup', 'concept', 'note', 'tutorial', 'question'] order by createTime desc limit " + num;
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
