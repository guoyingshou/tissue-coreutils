package com.tissue.core.social.dao.orient;

import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.orient.dao.DuplicateEmailException;
import com.tissue.core.util.OrientIdentityUtil;
//import com.tissue.core.util.OrientDataSource;
import com.tissue.core.mapper.InvitationMapper;
import com.tissue.core.social.Invitation;
import com.tissue.core.social.User;
import com.tissue.core.social.dao.InvitationDao;
import com.tissue.core.social.dao.UserDao;

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
import com.orientechnologies.orient.core.record.impl.ODocument;
/**
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;
*/

@Component
public class InvitationDaoImpl extends OrientDao implements InvitationDao {

    /**
    @Autowired
    private OrientDataSource dataSource;
    */

    public void inviteFriend(String fromId, String toId, String content) {

        String ridFrom = OrientIdentityUtil.decode(fromId);
        String ridTo = OrientIdentityUtil.decode(toId);
        
        String sql = "create edge EdgeFriend from " + ridFrom + " to " + ridTo + " set status = 'invite', createTime = sysdate(), content = '" + content + "'";

        OGraphDatabase db = dataSource.getDB();
        try {
            executeCommand(db, sql);
            /**
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
            */
        }
        catch(Exception exc) {
            exc.printStackTrace();
           //to do
        }
        finally {
            db.close();
        }
    }

    public void declineInvitation(String invitationId) {
        String rid = OrientIdentityUtil.decode(invitationId);
        
        String sql = "update " + rid + " set status = 'declined', updateTime = sysdate()";

        OGraphDatabase db = dataSource.getDB();
        try {
            executeCommand(db, sql);
            /**
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
            */
        }
        catch(Exception exc) {
            //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
    }

    public void acceptInvitation(String invitationId) {
        String rid = OrientIdentityUtil.decode(invitationId);
        
        String sql = "update " + rid + " set status = 'accepted', updateTime = sysdate()";

        OGraphDatabase db = dataSource.getDB();
        try {
            executeCommand(db, sql);
            /**
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
            */
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
