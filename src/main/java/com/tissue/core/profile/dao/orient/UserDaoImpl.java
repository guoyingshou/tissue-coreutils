package com.tissue.core.profile.dao.orient;

import com.tissue.core.converter.UserConverter;
import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.profile.User;
import com.tissue.core.profile.dao.UserDao;
import com.tissue.core.profile.dao.DuplicateEmailException;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;

@Component
public class UserDaoImpl implements UserDao {

    private static String byEmail = "select from User where email = ?";

    @Autowired
    private OrientDataSource dataSource;

    public User create(User user) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = new ODocument("User");
            doc.field("username", user.getUsername());
            doc.field("password", user.getPassword());
            doc.field("email", user.getEmail());
            doc.field("displayName", user.getDisplayName());
            doc.field("createTime", user.getCreateTime());
            doc.save();

            user.setId(OrientIdentityUtil.encode(doc.getIdentity().toString()));
        }
        catch(Exception exc) {
            //todo
            exc.printStackTrace();
        }
        finally {
            db.close();
        }

        return user;
    }

    public User update(User user) {
        return null;
    }

    public User getUserById(String id) {
        User user = null;

        OGraphDatabase db = dataSource.getDB();
        try {

            ORecordId record = new ORecordId(OrientIdentityUtil.decode(id));
            ODocument doc = db.load(record);
            if(doc != null) {

                String username = doc.field("username", String.class);
                String displayName = doc.field("displayName", String.class);

                user = new User();
                user.setId(id);

                user.setUsername(username);
                user.setDisplayName(displayName);
            }
        }
        catch(Exception exc) {
            //to do: must process this exception
        }
        finally {
            db.close();
        }
        return user;
   }

    public User getUserByEmail(String email) {
        User user = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery(byEmail);
            List<ODocument> result = db.command(query).execute(email);
            if(result.size() == 1) {
                ODocument doc = result.get(0);
                user = new User();
                user.setId(OrientIdentityUtil.encode(doc.getIdentity().toString()));

                user.setUsername(doc.field("username").toString());
                user.setDisplayName(doc.field("displayName").toString());
            }
        }
        catch(Exception exc) {
           //to do
        }
        finally {
            db.close();
        }
        return user;
    }

    public List<User> getFriends(String viewerId) {
        List<User> users = new ArrayList();

        String rid = OrientIdentityUtil.decode(viewerId);
        OGraphDatabase db = dataSource.getDB();
        try {
            Set<OIdentifiable> inEdges = db.getInEdges(new ORecordId(rid), "friend");
            for(OIdentifiable id : inEdges) {
                ODocument friendDoc = new ODocument("EdgeFriend", id.getIdentity());
                ODocument userDoc = friendDoc.field("out");
                User user = UserConverter.buildUser(userDoc);
                users.add(user);
            }

            Set<OIdentifiable> outEdges = db.getOutEdges(new ORecordId(rid), "friend");
            for(OIdentifiable id : outEdges) {
                ODocument friendDoc = new ODocument("EdgeFriend", id.getIdentity());
                ODocument userDoc = friendDoc.field("in");
                User user = UserConverter.buildUser(userDoc);
                users.add(user);
            }
        }
        catch(Exception exc) {
           //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return users;
    }

    public boolean isFriend(String userId1, String userId2) {
        boolean isFriend = false;

        OGraphDatabase db = dataSource.getDB();
        try {
            OIdentifiable id1 = new ORecordId(OrientIdentityUtil.decode(userId1));
            OIdentifiable id2 = new ORecordId(OrientIdentityUtil.decode(userId2));

            String[] labels = {"friend"};
            Set<OIdentifiable> edges = db.getEdgesBetweenVertexes(id1, id2, labels);
            if(edges.size() > 0) {
                isFriend = true;
            }
        }
        catch(Exception exc) {
           //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return isFriend;
    }

    public void getEdges(String fromId, String toId) {
        OGraphDatabase db = dataSource.getDB();
        try {
            OIdentifiable from = new ORecordId(OrientIdentityUtil.decode(fromId));
            OIdentifiable to = new ORecordId(OrientIdentityUtil.decode(toId));

            String[] classNames11 = {"EdgeInvite", "EdgeFriend"};
            Set<OIdentifiable> edges11 = db.getEdgesBetweenVertexes(from, to, null, classNames11);

            for(OIdentifiable id : edges11) {
                 ODocument doc = db.load(id.getIdentity());
            }

        }
        catch(Exception exc) {
           //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
    }
}
