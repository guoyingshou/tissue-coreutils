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
        String sql = "create edge EdgeInvite from " + command.getAccount().getId() + " to " + command.getTo().getId() + " set category = 'invitation', createTime = sysdate(), content = '" + command.getContent() + "'";
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

                ODocument toDoc = doc.field("in");
                User to = UserMapper.buildUser(toDoc);
                invitation.setTo(to);
            }
        }
        finally {
            db.close();
        }
        return invitation;
    }

    public List<Invitation> getInvitationsReceived(String accountId) {
        String sql = "select from EdgeInvite where category = 'invitation' and " + accountId + " in in.accounts";
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

    public void declineInvitation(Invitation invitation) {
        String sql = "update " + invitation.getId() + " set category = 'declined', updateTime = sysdate()";
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

        String sql = "update " + invitation.getId() + " set category = 'accepted', updateTime = sysdate()";
        logger.debug(sql);

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            String fromId = invitation.getAccount().getUser().getId();
            String toId = invitation.getTo().getId();

            sql = "create edge EdgeConnect from " + toId + " to " + fromId + " set category = 'friend', updateTime = sysdate()";
            logger.debug(sql);
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            sql = "update " + fromId + " increment inviteLimit = -1";
            logger.debug(sql);
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
    }

}
