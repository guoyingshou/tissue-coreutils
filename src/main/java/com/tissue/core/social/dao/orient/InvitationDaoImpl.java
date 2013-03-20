package com.tissue.core.social.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.User;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.social.mapper.InvitationMapper;
import com.tissue.core.social.command.InvitationCommand;
import com.tissue.core.social.Invitation;
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

    private static Logger logger = LoggerFactory.getLogger(InvitationDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public String create(InvitationCommand command) {
        String sql = "create edge EdgeInvitation from " + command.getAccount().getId() + " to " + command.getTo().getId() + " set label = 'invite', createTime = sysdate(), content = '" + command.getContent() + "'";
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
    public Boolean isInvitable(User owner, Account viewerAccount) {
        Boolean invitable = true;
        String sql = "select from EdgeInvitation where (out in " + viewerAccount.getId() + " and in in " + owner.getId() + ") or (out.user in " + owner.getId()  + " and in.accounts contains " + viewerAccount.getId();
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
