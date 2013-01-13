package com.tissue.core.security.dao.orient;

import com.tissue.core.social.User;
import com.tissue.core.social.Invitation;
import com.tissue.core.mapper.UserDetailsMapper;
import com.tissue.core.mapper.UserMapper;
import com.tissue.core.mapper.InvitationMapper;
import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.security.UserDetailsImpl;
import com.tissue.core.security.dao.UserDetailsDao;

import java.util.List;
import java.util.Date;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Arrays;
import java.nio.charset.Charset;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.record.impl.ODocument;

@Component
public class UserDetailsDaoImpl implements UserDetailsDao {


    @Autowired
    private OrientDataSource dataSource;

    public void setDataSource(OrientDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public UserDetailsImpl getUserByUsername(String username) {
        UserDetailsImpl userDetails = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            String sqlUsername = "select from User where username = ?";
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery(sqlUsername);
            List<ODocument> result = db.command(query).execute(username);
            if(result != null) {
                ODocument doc = result.get(0);
                userDetails = UserDetailsMapper.buildUser(doc);

                String rid = doc.getIdentity().toString();
                String sql = "select from EdgeFriend where in in " + rid + " or out in " + rid;
                List<ODocument> friendsDoc = db.query(new OSQLSynchQuery(sql));
                for(ODocument edgeDoc : friendsDoc) {
                    String status = edgeDoc.field("status", String.class);
                    List<String> ins = Arrays.asList("invite", "declined");
                    if(ins.contains(status)) {
                        Invitation invitation = InvitationMapper.buildInvitation(edgeDoc);
                        userDetails.addInvitation(invitation);
                    }
                    if("accepted".equals(status)) {
                        ODocument inDoc = edgeDoc.field("in");
                        if(rid.equals(inDoc.getIdentity().toString())) {
                            ODocument outDoc = edgeDoc.field("out");
                            User friend = UserMapper.buildUser(outDoc);
                            userDetails.addFriend(friend);
                        }
                        else {
                            User friend = UserMapper.buildUser(inDoc);
                            userDetails.addFriend(friend);
                        }
                    }
                }


                /**
                user = new UserDetailsImpl();
                user.setUsername(username);
                user.setPassword(doc.field("password").toString());
                user.setDisplayName(doc.field("displayName").toString());

                user.setId(OrientIdentityUtil.encode(doc.getIdentity().toString()));
                */
            }
        }
        catch(Exception exc) {
            //to do:
            exc.printStackTrace();
        }
        finally {
            db.close();
        }

        return userDetails;
    }

}
