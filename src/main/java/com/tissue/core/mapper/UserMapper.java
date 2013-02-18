package com.tissue.core.mapper;

//import com.tissue.core.util.OrientIdentityUtil;

import com.tissue.core.social.command.UserCommand;
import com.tissue.core.social.User;
import com.tissue.core.social.Invitation;
import com.tissue.core.social.Impression;
import com.tissue.core.plan.Plan;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class UserMapper {

    public static ODocument convertUser(UserCommand userCommand) {
        ODocument doc = new ODocument("User");
        doc.field("username", userCommand.getUsername());
        doc.field("password", userCommand.getPassword());
        doc.field("displayName", userCommand.getDisplayName());
        doc.field("headline", userCommand.getHeadline());
        doc.field("email", userCommand.getEmail());
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

        String email = userDoc.field("email", String.class);
        user.setEmail(email);

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
                if("plan".equals(label) || "members".equals(label)) {
                    ODocument planDoc = outEdgeDoc.field("in");
                    Plan plan = PlanMapper.buildPlanSelf(planDoc);

                    //plan == null means the containing topic has been deleted
                    if(plan != null) {
                        user.addPlan(plan);
                    }
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

    public static Invitation buildInvitationSelf(ODocument doc) {
        System.out.println("in user mapper: " + doc); 

        Invitation invitation = new Invitation();
        invitation.setId(doc.getIdentity().toString());

        String content = doc.field("content", String.class);
        invitation.setContent(content);

        Date createTime = doc.field("createTime", Date.class);
        invitation.setCreateTime(createTime);

        return invitation;
    }
}
