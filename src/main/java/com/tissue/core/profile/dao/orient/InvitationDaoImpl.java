package com.tissue.core.profile.dao.orient;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.converter.InvitationConverter;
import com.tissue.core.profile.Invitation;
import com.tissue.core.profile.User;
import com.tissue.core.profile.dao.InvitationDao;
import com.tissue.core.profile.dao.UserDao;
import com.tissue.core.profile.dao.DuplicateEmailException;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;

@Component
public class InvitationDaoImpl implements InvitationDao {

    @Autowired
    private OrientDataSource dataSource;

    public boolean canInvite(String fromId, String toId) {
        boolean result = true;

        String ridFrom = OrientIdentityUtil.decode(fromId);
        String ridTo = OrientIdentityUtil.decode(toId);

        OGraphDatabase db = dataSource.getDB();
        try {
            OIdentifiable from = new ORecordId(ridFrom);
            OIdentifiable to = new ORecordId(ridTo);

            String[] labels = {"invitation", "friend"};
            Set<OIdentifiable> edges = db.getEdgesBetweenVertexes(from, to, labels);
            if(edges.size() > 0) {
                result = false;
            }
        }
        catch(Exception exc) {
           //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return result;
    }

    public void inviteFriend(String fromId, String toId, String content) {
        
        String sql = "create edge EdgeInvite from " + 
                     OrientIdentityUtil.decode(fromId) + 
                     " to " +
                     OrientIdentityUtil.decode(toId) +
                     " set label = 'invitation', status = 'unread', createTime = sysdate(), content = '" +
                     content + "'";

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        catch(Exception exc) {
            exc.printStackTrace();
           //to do
        }
        finally {
            db.close();
        }
    }

    public Invitation getInvitation(String invitationId) {
        Invitation invitation = null;

        String sql = "select from " + OrientIdentityUtil.decode(invitationId);
        OGraphDatabase db = dataSource.getDB();
        try {
            OSQLSynchQuery q = new OSQLSynchQuery(sql);
            List<ODocument> result = db.query(q);
            if(result.size() > 0) {
                ODocument invitationDoc = result.get(0);
                invitation = InvitationConverter.buildInvitation(invitationDoc);
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
           //to do
        }
        finally {
            db.close();
        }

        return invitation;
    }

    public List<Invitation> getInvitations(String viewerId) {
        List<Invitation> invitations = new ArrayList();

        String sql = "select from EdgeInvite where in in " + 
                      OrientIdentityUtil.decode(viewerId) +
                      " and status = 'unread'";
        OGraphDatabase db = dataSource.getDB();
        try {
            OSQLSynchQuery q = new OSQLSynchQuery(sql);
            List<ODocument> result = db.query(q);
            for(ODocument invitationDoc : result) {
                Invitation invitation = InvitationConverter.buildInvitation(invitationDoc);
                invitations.add(invitation);
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
           //to do
        }
        finally {
            db.close();
        }
        return invitations;
    }

    public boolean declineInvitation(String invitationId) {
        boolean flag = true;
        
        String sql = "update "+ 
                     OrientIdentityUtil.decode(invitationId) + 
                     " set status = 'declined', updateTime = sysdate()";

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        catch(Exception exc) {
            flag = false;
            //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return flag;
    }

    public Invitation acceptInvitation(String invitationId) {

        Invitation invitation = null;
        
        String sql = "select from " + OrientIdentityUtil.decode(invitationId);

        OGraphDatabase db = dataSource.getDB();
        try {

            OSQLSynchQuery q = new OSQLSynchQuery(sql);
            List<ODocument> result = db.query(q);
            if(result.size() > 0) {
                ODocument invitationDoc = result.get(0);
                invitationDoc.field("updateTime", new Date());
                invitationDoc.field("status", "accepted");
                invitationDoc.save();

                ODocument invitorDoc = invitationDoc.field("out");
                ODocument inviteeDoc = invitationDoc.field("in");

                 //add friend
                String sqlAddFriend = "create Edge EdgeFriend from " + 
                                      invitorDoc.getIdentity().toString() + 
                                      " to " + 
                                      inviteeDoc.getIdentity().toString() + 
                                      " set label = 'friend', createTime = sysdate()";
                OCommandSQL cmdAddFriend = new OCommandSQL(sqlAddFriend);
                db.command(cmdAddFriend).execute();

                invitation = InvitationConverter.buildInvitation(invitationDoc);
           }
        }
        catch(Exception exc) {
            //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return invitation;
    }
 
}
