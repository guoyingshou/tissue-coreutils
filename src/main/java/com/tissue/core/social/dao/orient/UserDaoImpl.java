package com.tissue.core.social.dao.orient;

import com.tissue.core.exceptions.NoRecordFoundException;
import com.tissue.core.mapper.UserMapper;

import com.tissue.core.orient.dao.DuplicateEmailException;
import com.tissue.core.util.OrientDataSource;

import com.tissue.core.social.command.UserCommand;
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
            ODocument doc = UserMapper.convertUser(userCommand);
            db.save(doc);
            userId = doc.getIdentity().toString();
        }
        finally {
           db.close();
        }
        return userId;
    }

    public void update(UserCommand userCommand) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = db.load(new ORecordId(userCommand.getId()));
            if(doc == null) {
                throw new NoRecordFoundException(userCommand.getId());
            }
            doc.field("displayName", userCommand.getDisplayName());
            doc.field("headline", userCommand.getHeadline());
            db.save(doc);
        }
        finally {
           db.close();
        }
    }

    public void updateEmail(UserCommand userCommand) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = db.load(new ORecordId(userCommand.getId()));
            if(doc == null) {
                throw new NoRecordFoundException(userCommand.getId());
            }
            doc.field("email", userCommand.getEmail());
            db.save(doc);
        }
        finally {
           db.close();
        }
    }

    public void changePassword(UserCommand userCommand) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = db.load(new ORecordId(userCommand.getId()));
            if(doc == null) {
                throw new NoRecordFoundException(userCommand.getId());
            }
            doc.field("password", userCommand.getPassword());
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
    public User getUserById(String id) {
        User user = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            String sql = "select from " + id;
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

    public void inviteFriend(String fromId, String toId, String content) {
        String sql = "create edge EdgeFriend from " + fromId + " to " + toId + " set label = 'invite', createTime = sysdate(), content = '" + content + "'";

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
        String sql = "select @this as invitation, out as user from EdgeFriend where label = 'invite' and in in " + userId;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                ODocument invDoc = doc.field("invitation");
                Invitation invitation = UserMapper.buildInvitationSelf(invDoc);

                ODocument userDoc = doc.field("user");
                User user = UserMapper.buildUserSelf(userDoc);
                invitation.setInvitor(user);
                
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

    public void acceptInvitation(String id) {
        String sql = "update " + id + " set label = 'friends', updateTime = sysdate()";
        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            int result = db.command(cmd).execute();
            if(result != 1) {
                throw new NoRecordFoundException(id);
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

    public boolean isUserIdExist(String userId) {
        boolean exist = false;
        String sql = "select from " + userId;
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
}
