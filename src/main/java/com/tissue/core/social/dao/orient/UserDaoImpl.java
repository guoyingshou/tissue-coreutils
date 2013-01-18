package com.tissue.core.social.dao.orient;

import com.tissue.core.mapper.UserMapper;
import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.orient.dao.DuplicateEmailException;
import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.social.User;
import com.tissue.core.social.Impression;
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

    public void addImpression(Impression impression) {

        String ridFrom = OrientIdentityUtil.decode(impression.getFrom().getId());
        String ridTo = OrientIdentityUtil.decode(impression.getTo().getId());

        String sql = "create edge from " + ridFrom + " to " + ridTo + " set label = 'impression', published = sysdate(), content = '" + impression.getContent() + "'";

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
        String sql = "select from EdgeImpression where in in " + rid;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            impressions = UserMapper.buildImpressions(docs);
        }
        finally {
            db.close();
        }
        return impressions;
    }

    public User getUserById(String id, boolean withConnections) {
        User user = null;
        String rid = OrientIdentityUtil.decode(id);
        String sql = "select @rid, displayName, resume from " + rid;
        if(withConnections) {
            sql = "select @rid,displayName,resume,union(in[@class='EdgeFriend'], out[@class='EdgeFriend']) as connections from " + rid;
        }

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = querySingle(db, sql);
            user = UserMapper.buildUser(doc, true);
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return user;
    }

    public User getUserByEmail(String email) {
        User user = null;
        String sql = "select from User where email = ?";

        OGraphDatabase db = dataSource.getDB();
        try {
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

}
