package com.tissue.core.dao.orient;

import com.tissue.core.User;
import com.tissue.core.dao.UserDao;
import com.tissue.core.command.UserCommand;
import com.tissue.core.command.ProfileCommand;
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

    /**
    public String create(UserCommand userCommand) {
        String accountId;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument accountDoc = AccountMapper.convertAccount(userCommand);
            accountDoc.save();

            ODocument userDoc = UserMapper.convertUser(userCommand);
            List accounts = new ArrayList();
            accounts.add(accountDoc.getIdentity());
            userDoc.field("accounts", accounts);
            userDoc.save();

            accountDoc.field("user", userDoc.getIdentity());
            accountDoc.save();

            accountId = accountDoc.getIdentity().toString();
        }
        finally {
           db.close();
        }
        return accountId;
    }
    */

    public void updateProfile(ProfileCommand command) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = db.load(new ORecordId(command.getAccount().getUser().getId()));
            doc.field("displayName", command.getDisplayName());
            doc.field("headline", command.getHeadline());
            db.save(doc);
        }
        finally {
           db.close();
        }
    }
 
    /**
    public void updateEmail(EmailCommand command) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = db.load(new ORecordId(command.getAccount().getId()));
            doc.field("email", command.getEmail());
            db.save(doc);
        }
        finally {
           db.close();
        }
    }

    public void updatePassword(PasswordCommand command) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = db.load(new ORecordId(command.getAccount().getId()));
            doc.field("password", Hashing.md5().hashString(command.getPassword(), Charset.forName("utf-8")).toString());
            db.save(doc);
        }
        finally {
           db.close();
        }
    }

    public Account getAccount(String accountId) {
        String sql = "select from " + accountId;
        logger.debug(sql);

        Account account = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                account = AccountMapper.buildAccount(doc);
            }
        }
        finally {
            db.close();
        }
        return account;
    }


    */

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

    public List<User> getFriends(String userId) {
        String sql = "select in[label='friend'].out, out[label='friend'].in from " + userId;
        logger.debug(sql);

        List<User> friends = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {

                ODocument inDoc = doc.field("in");
                if(inDoc != null) {
                    User user = UserMapper.buildUserSelf(inDoc);
                    friends.add(user);
                }

                ODocument outDoc = doc.field("out");
                if(outDoc != null) {
                    User user = UserMapper.buildUserSelf(outDoc);
                    friends.add(user);
                }
            }
        }
        finally {
            db.close();
        }
        return friends;
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
                User user = UserMapper.buildUserSelf(doc);
                users.add(user);
            }
        }
        finally {
            db.close();
        }
        return users;
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

    /**
    public boolean isUsernameExist(String username) {
        String sql = "select from account where username = '" + username + "'";
        logger.debug(sql);

        boolean exist = false;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(docs.size() > 0) {
               exist = true;
            }
        }
        finally {
            db.close();
        }
        return exist;
    }

    public boolean isEmailExist(String email) {
        String sql = "select from account where email = '" + email + "'";
        logger.debug(sql);

        boolean exist = false;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(docs.size() > 0) {
               exist = true;
            }
        }
        finally {
            db.close();
        }
        return exist;
    }

    public boolean isEmailExist(String excludingUserId, String email) {
        String sql = "select from account where email = '" + email + "' and @rid <> " + excludingUserId;
        logger.debug(sql);

        boolean exist = false;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(docs.size() > 0) {
               exist = true;
            }
        }
        finally {
            db.close();
        }
        return exist;
    }
    */

}
