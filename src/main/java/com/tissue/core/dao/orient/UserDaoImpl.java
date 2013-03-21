package com.tissue.core.dao.orient;

import com.tissue.core.User;
import com.tissue.core.dao.UserDao;
import com.tissue.core.command.UserCommand;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.mapper.UserMapper;

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
public class UserDaoImpl implements UserDao {

    private static Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public void updateProfile(UserCommand command) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = db.load(new ORecordId(command.getId()));
            doc.field("displayName", command.getDisplayName());
            doc.field("headline", command.getHeadline());
            db.save(doc);
        }
        finally {
           db.close();
        }
    }
 
    public User getUser(String userId) {
        String sql = "select from " + userId;
        logger.debug(sql);

        User user = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                user = UserMapper.buildUser(doc);
            }
        }
        finally {
            db.close();
        }
        return user;
    }

    public User getUserByAccount(String accountId) {
        String sql = "select from user where accounts in " + accountId;
        logger.debug(sql);

        User user = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                user = UserMapper.buildUser(doc);
            }
        }
        finally {
            db.close();
        }
        return user;
    }

    /**
    public String getUserIdByAccount(String accountId) {
        String sql = "select from user where accounts in " + accountId;
        logger.debug(sql);

        String userId = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                userId = doc.getIdentity().toString();
            }
        }
        finally {
            db.close();
        }
        return userId;
    }
    */

    public List<User> getFriends(String userId) {
        String sql = "select set(in[label='friend'].out, out[label='friend'].in) as friends from " + userId;
        logger.debug(sql);

        List<User> friends = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                List<ODocument> friendsDoc = docs.get(0).field("friends");
                for(ODocument doc : friendsDoc) {
                    User user = UserMapper.buildUser(doc);
                    friends.add(user);
                }
            }
        }
        finally {
            db.close();
        }
        return friends;
    }

    public Boolean isFriend(String userId1, String userId2) {
        String sql = "select from EdgeFriend where label in 'friend' and ((out in " + userId1 + " and in in " + userId2 + ") or (out in " + userId2 + " and in in " + userId1 + "))";
        logger.debug(sql);

        Boolean friend = false;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(docs.size() > 0) {
               friend = true;
            }
        }
        finally {
            db.close();
        }
        return friend;
    }

    public List<User> getNewUsers(String excludingUserId, int limit) {
        String sql = "select from user order by createTime desc limit " + limit;
        if(excludingUserId != null) {
            sql = "select from user where @this not in " + excludingUserId + " order by createTime desc limit " + limit;
        }
        logger.debug(sql);

        List<User> users = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                User user = UserMapper.buildUser(doc);
                users.add(user);
            }
        }
        finally {
            db.close();
        }
        return users;
    }

}
