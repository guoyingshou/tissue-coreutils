package com.tissue.core.mapper;

import com.tissue.core.command.UserCommand;
import com.tissue.core.social.User;
import com.tissue.core.social.Invitation;
import com.tissue.core.social.Impression;
import com.tissue.core.social.About;
import com.tissue.core.plan.Plan;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class UserMapper {

    public static ODocument convertUser(UserCommand user) {
        ODocument doc = new ODocument("User");
        doc.field("displayName", user.getDisplayName());
        doc.field("headline", user.getHeadline());
        doc.field("createTime", new Date());
        doc.field("inviteLimit", 32);
        return doc;
    }

    public static User buildUserSelf(ODocument userDoc) {
        User user = new User();
        String rid = userDoc.getIdentity().toString();
        user.setId(rid);

        String displayName = userDoc.field("displayName", String.class);
        user.setDisplayName(displayName);

        String headline = userDoc.field("headline", String.class);
        user.setHeadline(headline);

        String resume = userDoc.field("resume", String.class);
        user.setResume(resume);

        return user;
    }

    public static User buildUser(ODocument doc) {
        User user = buildUserSelf(doc);
 
        Set<ODocument> outEdgesDoc = doc.field("out");
        if(outEdgesDoc != null) {
            for(ODocument outEdgeDoc : outEdgesDoc) {
                String label = outEdgeDoc.field("label", String.class);
                if("hostGroup".equals(label) || "joinGroup".equals(label)) {
                    ODocument planDoc = outEdgeDoc.field("in");
                    Plan plan = PlanMapper.buildPlanSelf(planDoc);
                }
             }
        }
        return user;
    }

    public static Impression buildImpressionSelf(ODocument impressionDoc) {

        Impression impression = new Impression();
        impression.setId(impressionDoc.getIdentity().toString());

        String content = impressionDoc.field("content", String.class);
        impression.setContent(content);

        Date createTime = impressionDoc.field("createTime", Date.class);
        impression.setCreateTime(createTime);

        return impression;
    }

    /**
    public static Invitation buildInvitationSelf(ODocument doc) {
        ODocument fromDoc = doc.field("out");
        ODocument fromUserDoc = fromDoc.field("user");
        User invitor = UserMapper.buildUserSelf(fromUserDoc);

        //"in" is a user
        ODocument toUserDoc = doc.field("in");
        User invitee = UserMapper.buildUserSelf(toUserDoc);

        String content = doc.field("content", String.class);
        Date createTime = doc.field("createTime", Date.class);

        Invitation invitation = new Invitation();
        invitation.setId(doc.getIdentity().toString());
        invitation.setInvitor(invitor);
        invitation.setInvitee(invitee);
        invitation.setContent(content);
        invitation.setCreateTime(createTime);

        return invitation;
    }
    */

}
