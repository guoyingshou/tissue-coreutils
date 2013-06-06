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

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

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
        OrientGraph db = dataSource.getDB();
        try {
            OrientVertex v = db.getVertex(command.getId());
            v.setProperty("displayName", command.getDisplayName());
            v.setProperty("headline", command.getHeadline());
        }
        finally {
           db.shutdown();
        }
    }
 
    public User getUser(String userId) {
        String sql = "select from " + userId;
        logger.debug(sql);

        User user = null;

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.getRawGraph().command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            for(ODocument doc : docs) {
                user = UserMapper.buildUser(doc);
            }
        }
        finally {
            db.shutdown();
        }
        return user;
    }

    public List<User> getFriends(String userId) {
        String sql = "select set(in_Friend.out, out_Friend.in) as friends from " + userId;
        logger.debug(sql);

        List<User> friends = new ArrayList();
        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            for(ODocument doc : docs) {
                Set<ODocument> friendsDoc = doc.field("friends", Set.class);
                for(ODocument friendDoc : friendsDoc) {
                    User user = UserMapper.buildUser(friendDoc);
                    friends.add(user);
                }
            }
        }
        finally {
            db.shutdown();
        }
        return friends;
    }

    public Boolean isFriend(String userId1, String userId2) {
        String sql = "select from " +  userId1 + 
                     " where set(in_Friend.out, out_Friend.in) in " + userId2;
        logger.debug(sql);

        Boolean friend = false;

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.getRawGraph().command(new OSQLSynchQuery(sql)).execute();
            for(ODocument doc : docs) {
               friend = true;
               break;
            }
        }
        finally {
            db.shutdown();
        }
        return friend;
    }

    public void removeRelation(String userId1, String userId2) {
        String sql = "delete edge from " + userId1 + " to " + userId2;
        logger.debug(sql);

        OrientGraph db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.shutdown();
        }
    }

    public List<User> getNewUsers(String excludingAccountId, int limit) {

        StringBuilder buf = new StringBuilder("select out_AccountsUser as user from account where out_AccountsUser.status is null ");
        if(excludingAccountId != null) {
            buf.append(" and @this not in " + excludingAccountId);
        }
        buf.append(" order by createTime desc limit " + limit);

        String sql = buf.toString();
        logger.debug(sql);

        List<User> users = new ArrayList();

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.getRawGraph().command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            for(ODocument doc : docs) {
                ODocument userDoc = doc.field("user");
                User user = UserMapper.buildUser(userDoc);
                users.add(user);
            }
        }
        finally {
            db.shutdown();
        }
        return users;
    }

    /**
     * The invitation's out property is a link to an account while in property
     * is a link to a user.
     */
    public Boolean isInvitable(String ownerId, Account viewerAccount) {
        Boolean invitable = true;

        String viewerAccountId = viewerAccount.getId();

        String sql = "select from " + viewerAccountId +
                     " where set(out_Invite.in, out_AccountsUser.in_Invite.out.out_AccountsUser) in " + ownerId;
                   
        logger.debug(sql);

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.getRawGraph().command(new OSQLSynchQuery(sql)).execute();
            for(ODocument doc : docs) {
               invitable = false;
               break;
            }
        }
        finally {
            db.shutdown();
        }
        return invitable;
    }

}
