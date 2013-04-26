package com.tissue.core.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.User;
import com.tissue.core.datasources.OrientDataSource;
import com.tissue.core.dao.UserDao;
import com.tissue.core.command.UserCommand;
import com.tissue.core.mapper.UserMapper;

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
public class UserDaoImpl implements UserDao {

    private static Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public void updateHeadline(UserCommand command) {
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

    public List<User> getFriends(String userId) {
        String sql = "select set(in[catetory='friend'].out, out[category='friend'].in) as friends from " + userId;
        logger.debug(sql);

        List<User> friends = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                Set<ODocument> friendsDoc = docs.get(0).field("friends", Set.class);
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
        String users = "[" + userId1 + "," + userId2 + "]";
        String sql = "select from EdgeConnect where category in 'friend' and out in " + users + " and in in " + users;
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

    public void removeRelation(String userId1, String userId2) {
        String sql = "delete edge from " + userId1 + " to " + userId2;
        logger.debug(sql);

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
    }

    public List<User> getNewUsers(String excludingAccountId, int limit) {
        String sql = "select user from account where user.status is null order by createTime desc limit " + limit;
        if(excludingAccountId != null) {
            sql = "select user from account where user.status is null and @this not in " + excludingAccountId + " order by createTime desc limit " + limit;
        }
        logger.debug(sql);

        List<User> users = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                ODocument userDoc = doc.field("user");
                User user = UserMapper.buildUser(userDoc);
                users.add(user);
            }
        }
        finally {
            db.close();
        }
        return users;
    }

    /**
     * The invitation's out property is a link to an account while in property
     * is a link to a user.
     */
    public Boolean isInvitable(String ownerId, Account viewerAccount) {
        Boolean invitable = true;

        String fromUsers = "[" + viewerAccount.getUser().getId() + ", " + ownerId + "]";
        String toUsers = "[" + viewerAccount.getUser().getId() + ", " + ownerId + "]";

        String sql = "select from EdgeInvite where out.user in " + fromUsers + " and in in " + toUsers;
        logger.debug(sql);

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(docs.size() > 0) {
               invitable = false;
            }
        }
        finally {
            db.close();
        }
        return invitable;
    }

}
