package com.tissue.core.social.dao.orient;

import com.tissue.core.mapper.UserMapper;
import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.orient.dao.DuplicateEmailException;
import com.tissue.core.util.OrientIdentityUtil;
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
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;

@Component
public class UserDaoImpl extends OrientDao implements UserDao {

    public User create(User user) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = UserMapper.convertUser(user);
            String id = saveDoc(doc);
            user.setId(id);
        }
        finally {
           db.close();
        }
        return user;
    }

    public User update(User user) {
        return null;
    }

    /**
     * @param id user id
     * @return a user with basic info plus plans he created or joined
     */
    public User getUserById(String id) {
        User user = null;
        String rid = OrientIdentityUtil.decode(id);

        OGraphDatabase db = dataSource.getDB();
        try {
            String sql = "select from " + rid;
            ODocument doc = querySingle(db, sql);
            user = UserMapper.buildUser(doc);
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return user;
    }

    public List<User> getFriends(String userId) {
        List<User> friends = new ArrayList();

        String rid = OrientIdentityUtil.decode(userId);

        OGraphDatabase db = dataSource.getDB();
        try {
            String sql = "select union(in[label='friends'].out, out[label='friends'].in) as friends from " + rid;
            ODocument doc = querySingle(db, sql);

            List<ODocument> friendsDoc = doc.field("friends");
            for(ODocument friendDoc : friendsDoc) {
                System.out.println(friendDoc);
                User friend = UserMapper.buildUserSelf(friendDoc);
                friends.add(friend);
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return friends;
    }
 
    public void addResume(String userId, String content) {
        String rid = OrientIdentityUtil.decode(userId);
        String sql = "update " + rid + " set resume = '" + content + "'";
        OGraphDatabase db = dataSource.getDB();
        try {
            executeCommand(db, sql);
        }
        finally {
            db.close();
        }
    }

    public void inviteFriend(String fromId, String toId, String content) {

        String ridFrom = OrientIdentityUtil.decode(fromId);
        String ridTo = OrientIdentityUtil.decode(toId);
        
        String sql = "create edge from " + ridFrom + " to " + ridTo + " set label = 'invite', createTime = sysdate(), content = '" + content + "'";

        OGraphDatabase db = dataSource.getDB();
        try {
            executeCommand(db, sql);
        }
        catch(Exception exc) {
            exc.printStackTrace();
           //to do
        }
        finally {
            db.close();
        }
    }

    public List<Invitation> getInvitationsReceived(String userId) {
        List<Invitation> invitations = new ArrayList();

        String rid = OrientIdentityUtil.decode(userId);
        String sql = "select from ographedge where label = 'invite' and in in " + rid;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            for(ODocument doc : docs) {
                Invitation invitation = UserMapper.buildInvitation(doc);
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

        String rid = OrientIdentityUtil.decode(userId);
        String sql = "select from ographedge where label = 'invite' and out in " + rid;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            for(ODocument doc : docs) {
                Invitation invitation = UserMapper.buildInvitation(doc);
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
        String rid = OrientIdentityUtil.decode(id);
        
        String sql = "update " + rid + " set label = 'declined', updateTime = sysdate()";

        OGraphDatabase db = dataSource.getDB();
        try {
            executeCommand(db, sql);
        }
        catch(Exception exc) {
            //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
    }

    public void acceptInvitation(String id) {
        String rid = OrientIdentityUtil.decode(id);
        
        String sql = "update " + rid + " set label = 'friends', updateTime = sysdate()";

        OGraphDatabase db = dataSource.getDB();
        try {
            executeCommand(db, sql);
        }
        catch(Exception exc) {
            //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
    }

    public void addImpression(Impression impression) {

        String ridFrom = OrientIdentityUtil.decode(impression.getFrom().getId());
        String ridTo = OrientIdentityUtil.decode(impression.getTo().getId());

        String sql = "create edge from " + ridFrom + " to " + ridTo + " set label = 'impression', createTime = sysdate(), content = '" + impression.getContent() + "'";

        OGraphDatabase db = dataSource.getDB();
        try {
            executeCommand(db, sql);
        }
        finally {
            db.close();
        }
    }

    public List<Impression> getImpressions(String userId) {
        List<Impression> impressions = new ArrayList();

        String rid = OrientIdentityUtil.decode(userId);
        String sql = "select from ographedge where label = 'impression' and in in " + rid;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            for(ODocument doc : docs) {
                Impression impression = UserMapper.buildImpression(doc);
                impressions.add(impression);
            }
        }
        finally {
            db.close();
        }
        return impressions;
    }

}
