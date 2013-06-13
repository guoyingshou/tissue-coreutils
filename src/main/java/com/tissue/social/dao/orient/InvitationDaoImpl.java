package com.tissue.social.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.User;
import com.tissue.core.datasources.OrientDataSource;
import com.tissue.core.mapper.UserMapper;
import com.tissue.social.mapper.InvitationMapper;
import com.tissue.social.command.InvitationCommand;
import com.tissue.social.Invitation;
import com.tissue.social.dao.InvitationDao;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

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
public class InvitationDaoImpl implements InvitationDao {

    private static Logger logger = LoggerFactory.getLogger(InvitationDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public String create(InvitationCommand command) {
        String accountId = command.getAccount().getId();
        String userId = command.getTo().getId();
        String content = command.getContent();

        String sql = "create edge Invites " +
                     "from " + accountId + 
                     " to " + userId + 
                     " set category = 'invitation', createTime = sysdate(), content = '" + content + "'";
        logger.debug(sql);

        OrientGraph db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            Object obj = db.command(cmd).execute();

            return obj.toString();
        }
        finally {
            db.shutdown();
        }
    }

    public Invitation getInvitation(String invitationId) {
        String sql = "select from " + invitationId;
        logger.debug(sql);

        Invitation invitation = null;

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.getRawGraph().command(new OSQLSynchQuery(sql)).execute();
            for(ODocument doc : docs) {
                invitation = InvitationMapper.buildInvitation(doc);

                ODocument toDoc = doc.field("in");
                User to = UserMapper.buildUser(toDoc);
                invitation.setTo(to);
            }
        }
        finally {
            db.shutdown();
        }
        return invitation;
    }

    public List<Invitation> getInvitationsReceived(String accountId) {
        String sql = "select @this as invites from Invites " +
                     "where category = 'invitation' " +
                     "and in.in_Belongs in " + accountId;
        logger.debug(sql);

        List<Invitation> invitations = new ArrayList();

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:2")).execute();
            for(ODocument doc : docs) {
                ODocument inviteDoc = doc.field("invites");
                Invitation invitation = InvitationMapper.buildInvitation(inviteDoc);
                invitations.add(invitation);
            }
        }
        finally {
            db.shutdown();
        }
        return invitations;
    }

    public void declineInvitation(Invitation invitation) {
        String sql = "update " + invitation.getId() + " set category = 'declined', updateTime = sysdate()";
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

    public void acceptInvitation(Invitation invitation) {

        String sql = "update " + invitation.getId() + " set category = 'accepted', updateTime = sysdate()";
        logger.debug(sql);

        OrientGraph db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            String fromId = invitation.getAccount().getUser().getId();
            String toId = invitation.getTo().getId();

            sql = "create edge Friends from " + toId + " to " + fromId + " set category = 'friend', updateTime = sysdate()";
            logger.debug(sql);
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            sql = "update " + fromId + " increment inviteLimit = -1";
            logger.debug(sql);
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.shutdown();
        }
    }

}
