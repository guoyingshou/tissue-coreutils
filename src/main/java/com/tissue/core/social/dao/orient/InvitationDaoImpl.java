package com.tissue.core.social.dao.orient;

import com.tissue.core.command.UserCommand;
import com.tissue.core.command.ProfileCommand;
import com.tissue.core.command.EmailCommand;
import com.tissue.core.command.PasswordCommand;
import com.tissue.core.command.InvitationCommand;
import com.tissue.core.command.ImpressionCommand;
import com.tissue.core.exceptions.NoRecordFoundException;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.mapper.TopicMapper;
import com.tissue.core.mapper.PlanMapper;
import com.tissue.core.mapper.PostMapper;
import com.tissue.core.mapper.UserMapper;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.core.mapper.InvitationMapper;
import com.tissue.core.mapper.ActivityStreamMapper;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Post;
import com.tissue.core.social.Account;
import com.tissue.core.social.User;
import com.tissue.core.social.Impression;
import com.tissue.core.social.Invitation;
import com.tissue.core.social.Activity;
import com.tissue.core.social.About;
import com.tissue.core.social.dao.InvitationDao;

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
public class InvitationDaoImpl implements InvitationDao {

    private static Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public String create(InvitationCommand command) {
        String sql = "create edge EdgeInvitation from " + command.getFrom().getId() + " to " + command.getTo().getId() + " set label = 'invite', createTime = sysdate(), content = '" + command.getContent() + "'";
        logger.debug(sql);

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            Object obj = db.command(cmd).execute();

            return obj.toString();
        }
        finally {
            db.close();
        }
    }

    public Invitation getInvitation(String invitationId) {
        String sql = "select from " + invitationId;
        logger.debug(sql);

        Invitation invitation = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                invitation = InvitationMapper.buildInvitation(doc);
            }
        }
        finally {
            db.close();
        }
        return invitation;
    }

    public List<Invitation> getInvitationsReceived(String userId) {
        String sql = "select from EdgeInvitation where label = 'invite' and in in " + userId;
        logger.debug(sql);

        List<Invitation> invitations = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                Invitation invitation = InvitationMapper.buildInvitation(doc);
                invitations.add(invitation);
            }
        }
        finally {
            db.close();
        }
        return invitations;
    }

    /**
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
    */

    public void declineInvitation(Invitation invitation) {
        String sql = "update " + invitation.getId() + " set label = 'declined', updateTime = sysdate()";
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

    public void acceptInvitation(Invitation invitation) {

        String sql = "update " + invitation.getId() + " set label = 'accepted', updateTime = sysdate()";
        logger.debug(sql);

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "create edge EdgeFriend from " + invitation.getTo().getId() + " to " + invitation.getFrom().getUser().getId() + " set label = 'friend', updateTime = sysdate()";
            logger.debug(sql);

            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
    }

    /**
     * The invitation's out property is a link to an account while in property
     * is a link to a user.
     */
    public Boolean isInvitable(String ownerId, Account viewerAccount) {
        Boolean invitable = true;
        String sql = "select from EdgeInvitation where (out in " + viewerAccount.getId() + " and in in " + ownerId + ") or (out.user in " + ownerId  + " and in.accounts contains " + viewerAccount.getId();
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
