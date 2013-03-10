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
import com.tissue.core.social.dao.UserDao;

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

    /**
     * @param id user id
     * @return a user with basic info plus plans he created or joined
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

    /**
     * @param id user id
     * @return a user with basic info plus plans he created or joined
     */
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

    /**
     * @param id user id
     * @return a user with basic info plus plans he created or joined
     */
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

    public void addResume(String userId, String content) {
        String sql = "update " + userId + " set resume = '" + content + "'";
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

    public List<User> getFriends(String userId) {
        String sql = "select union(in[label='friend'].out, out[label='friend'].in) as friends from " + userId;
        logger.debug(sql);

        List<User> friends = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                List<ODocument> friendsDoc = doc.field("friends");
                for(ODocument friendDoc : friendsDoc) {
                    User friend = UserMapper.buildUserSelf(friendDoc);
                    friends.add(friend);
                }
            }
        }
        finally {
            db.close();
        }
        return friends;
    }

    public List<Activity> getActivities(String userId, int num) {
        List<Activity> activities = new ArrayList();

        String sql = "select from EdgeAction where out.user in (select union(in[label='friend'].out, out[label='friend'].in) from " + userId + ") or (out in (select in.out from (select from plan where in.out.user in " + userId + ")) and out.user not in " + userId + ") order by createTime desc limit " + num;
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

    /**
     * -----------------
     */
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

    /**
     * topic
     */
    public List<Topic> getNewTopics(String excludingUserId, int limit) {
        String sql = "select from topic where deleted is null order by createTime desc limit " + limit;
        if(excludingUserId != null) {
            sql = "select from topic where deleted is null and in.out not in " + excludingUserId + " and plans.in.out not in " + excludingUserId + " order by createTime desc limit " + limit;
        }
        logger.debug(sql);

        List<Topic> topics = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                Topic topic = TopicMapper.buildTopic(doc);
                topics.add(topic);
            }
        }
        finally {
            db.close();
        }
        return topics;
    }

    /**
     * plan
     */
    public List<Plan> getPlans(String userId) {
        String sql = "select from plan where in.out.user in " + userId;
        logger.debug(sql);

        List<Plan> plans = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                Plan plan = PlanMapper.buildPlan(doc);
                plans.add(plan);
            }
        }
        finally {
            db.close();
        }
        return plans;
    }

    public List<Plan> getPlansByAccount(String accountId) {
        String sql = "select from plan where in.out in " + accountId;
        logger.debug(sql);

        List<Plan> plans = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                Plan plan = PlanMapper.buildPlan(doc);
                plans.add(plan);
            }
        }
        finally {
            db.close();
        }
        return plans;
    }

    /**
     * post
     */
    public long getPostsCount(String userId) {
        String sql = "select count(*) from Post where deleted is null and in.out.user in " + userId;
        logger.debug(sql);

        long count = 0;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                count = doc.field("count", long.class);
            }
        }
        finally {
            db.close();
        }
        return count;
    }

    public List<Post> getPagedPosts(String userId, int page, int size) {
        String sql = "select from Post where deleted is null and in.out.user in " + userId + " order by createTime desc skip " + (page - 1) * size + " limit " + size;
        logger.debug(sql);

        List<Post> posts = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                Post post = PostMapper.buildPost(doc);
                posts.add(post);
            }
        }
        finally {
            db.close();
        }
        return posts;
    }
}
