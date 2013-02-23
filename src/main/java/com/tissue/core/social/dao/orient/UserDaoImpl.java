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
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Post;
import com.tissue.core.social.Account;
import com.tissue.core.social.User;
import com.tissue.core.social.Impression;
import com.tissue.core.social.Invitation;
import com.tissue.core.social.dao.UserDao;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

@Component
public class UserDaoImpl implements UserDao {
    @Autowired
    protected OrientDataSource dataSource;

    public String create(UserCommand userCommand) {
        String userId;

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

            userId = userDoc.getIdentity().toString();
        }
        finally {
           db.close();
        }
        return userId;
    }

    public void updateProfile(ProfileCommand command) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = db.load(new ORecordId(command.getUser().getId()));
            if(doc == null) {
                throw new NoRecordFoundException(command.getUser().getId());
            }
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
            ODocument doc = db.load(new ORecordId(command.getUser().getId()));
            if(doc == null) {
                throw new NoRecordFoundException(command.getUser().getId());
            }
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
            ODocument doc = db.load(new ORecordId(command.getUser().getId()));
            if(doc == null) {
                throw new NoRecordFoundException(command.getUser().getId());
            }
            doc.field("password", command.getPassword());
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
        User user = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            String sql = "select from " + userId;
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
        User user = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            String sql = "select from user where accounts in " + accountId;
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
        String userId = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            String sql = "select from user where accounts in " + accountId;
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
        Account account = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            String sql = "select from " + accountId;
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
        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
    }

    public void inviteFriend(String fromAccountId, String toUserId, String content) {
        String sql = "create edge EdgeFriend from " + fromAccountId + " to " + toUserId + " set label = 'invite', createTime = sysdate(), content = '" + content + "'";

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
    }

    public List<Invitation> getInvitationsReceived(String userId) {
        List<Invitation> invitations = new ArrayList();
        //String sql = "select @this as invitation, out as user from EdgeFriend where label = 'invite' and in in " + userId;
        String sql = "select from EdgeFriend where label = 'invite' and in in " + userId;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                //Invitation invitation = UserMapper.buildInvitationSelf(doc);

                //"out" is an account
                ODocument fromDoc = doc.field("out");
                ODocument fromUserDoc = fromDoc.field("user");
                User invitor = UserMapper.buildUserSelf(fromUserDoc);

                //"in" is a user
                ODocument toUserDoc = doc.field("in");
                User invitee = UserMapper.buildUserSelf(toUserDoc);

                String content = doc.field("content", String.class);
                Date createTime = doc.field("createTime", Date.class);

                Invitation invitation = new Invitation();
                invitation.setId(doc.getIdentity().toString());
                invitation.setInvitor(invitor);
                invitation.setInvitee(invitee);
                invitation.setContent(content);
                invitation.setCreateTime(createTime);

                invitations.add(invitation);
            }
        }
        finally {
            db.close();
        }
        return invitations;
    }

    public List<Invitation> getInvitationsSent(String userId) {
        List<Invitation> invitations = new ArrayList();

        String sql = "select @this as invitation, in as user from EdgeFriend where label = 'invite' and out in " + userId;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                ODocument invDoc = doc.field("invitation");
                Invitation invitation = UserMapper.buildInvitationSelf(invDoc);

                ODocument userDoc = doc.field("user");
                User user = UserMapper.buildUserSelf(userDoc);
                invitation.setInvitee(user);

                invitations.add(invitation);
            }
        }
        finally {
            db.close();
        }
        return invitations;
    }

    /**
     * @param id id of an ographedge instance
     */
    public void declineInvitation(String id) {
        String sql = "update " + id + " set label = 'declined', updateTime = sysdate()";

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
         }
        finally {
            db.close();
        }
    }

    public void acceptInvitation(String invitationId) {
        String sql = "select from " + invitationId;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);

                //"out" refer to an accout
                ODocument fromDoc = doc.field("out");
                ODocument fromUserDoc = fromDoc.field("user");
                String fromUserId = fromUserDoc.getIdentity().toString();

                //"in" refer to an user
                ODocument toUserDoc = doc.field("in");
                String toUserId = toUserDoc.getIdentity().toString();

                sql = "update " + invitationId + " set label = 'accepted', updateTime = sysdate()";
                OCommandSQL cmd = new OCommandSQL(sql);
                db.command(cmd).execute();
 
                sql = "create edge EdgeFriend from " + fromUserId + " to " + toUserId + " set label = 'friend', updateTime = sysdate()";
                cmd = new OCommandSQL(sql);
                db.command(cmd).execute();
            }
        }
        finally {
            db.close();
        }
    }

    public void addImpression(Impression impression) {
        String fromId = impression.getFrom().getId();
        String toId = impression.getTo().getId();
        String sql = "create edge EdgeImpression from " + fromId + " to " + toId + " set label = 'impression', createTime = sysdate(), content = '" + impression.getContent() + "'";

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
         }
        finally {
            db.close();
        }
    }

    public List<Impression> getImpressions(String userId) {
        List<Impression> impressions = new ArrayList();
        String sql = "select @this as impression, out as user from EdgeImpression where in in " + userId;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                ODocument impDoc = doc.field("impression");
                Impression impression = UserMapper.buildImpressionSelf(impDoc);

                ODocument userDoc = doc.field("user");
                User user = UserMapper.buildUserSelf(userDoc);
                impression.setFrom(user);

                impressions.add(impression);
            }
        }
        finally {
            db.close();
        }
        return impressions;
    }

    public List<User> getFriends(String userId) {
        List<User> friends = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            String sql = "select union(in[label='friends'].out, out[label='friends'].in) as friends from " + userId;
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

    public List<User> getNewUsers(String excludingUserId, int limit) {
        List<User> users = new ArrayList();
        String sql = "select from user order by createTime desc limit " + limit;
        if(excludingUserId != null) {
            sql = "select from user where @this not in " + excludingUserId + " order by createTime desc limit " + limit;
        }

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

    public boolean isInvitable(String userId1, String userId2) {
        boolean invitable = true;
        String sql = "select from EdgeFriend where (label contains ['friends', 'invite']) and ((in in " + userId1 + " and out in " + userId2 + ") or (in in " + userId2 + " and out in " + userId1 + "))";

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

    public boolean isUsernameExist(String username) {
        boolean exist = false;
        String sql = "select from user where username = '" + username + "'";
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
        boolean exist = false;
        String sql = "select from user where email = '" + email + "'";
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
        boolean exist = false;
        String sql = "select from user where email = '" + email + "' and @rid <> " + excludingUserId;
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
        List<Topic> topics = new ArrayList();

        String sql = "select from topic where deleted is null order by createTime desc limit " + limit;
        if(excludingUserId != null) {
            sql = "select from topic where deleted is null and in.out not in " + excludingUserId + " and plans.in.out not in " + excludingUserId + " order by createTime desc limit " + limit;
        }

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
        List<Plan> plans = new ArrayList();
        String sql = "select from plan where in.out.user in " + userId;
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
        List<Plan> plans = new ArrayList();
        String sql = "select from plan where in.out in " + accountId;
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
        long count = 0;
        String sql = "select count(*) from Post where deleted is null and in.out.user in " + userId;

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
        List<Post> posts = new ArrayList();
        String sql = "select from Post where deleted is null and in.out.user in " + userId + " order by createTime desc skip " + (page - 1) * size + " limit " + size;

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
