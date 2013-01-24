package com.tissue.core.mapper;

import com.tissue.core.util.OrientIdentityUtil;
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

    public static ODocument convertUser(User user) {
        ODocument doc = new ODocument("User");
        doc.field("username", user.getUsername());
        doc.field("password", user.getPassword());
        doc.field("email", user.getEmail());
        doc.field("displayName", user.getDisplayName());
        doc.field("createTime", user.getCreateTime());
        return doc;
    }

    public static User buildUserSelf(ODocument userDoc) {
        User user = new User();
        String rid = userDoc.getIdentity().toString();
        user.setId(OrientIdentityUtil.encode(rid));

        String displayName = userDoc.field("displayName", String.class);
        user.setDisplayName(displayName);

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
                    user.addPlan(plan);
                }
             }
        }
        return user;
    }

    public static Impression buildImpressionSelf(ODocument impressionDoc) {

        Impression impression = new Impression();
        impression.setId(OrientIdentityUtil.encode(impressionDoc.getIdentity().toString()));

        String content = impressionDoc.field("content", String.class);
        impression.setContent(content);

        Date createTime = impressionDoc.field("createTime", Date.class);
        impression.setCreateTime(createTime);

        /**
        User from = new User();
        ODocument fromDoc = impressionDoc.field("out");
        from.setId(OrientIdentityUtil.encode(fromDoc.getIdentity().toString()));

        String fromDisplayName = fromDoc.field("displayName", String.class);
        from.setDisplayName(fromDisplayName);

        impression.setFrom(from);

        User to = new User();
        ODocument toDoc = impressionDoc.field("in");
        to.setId(OrientIdentityUtil.encode(toDoc.getIdentity().toString()));

        String toDisplayName = toDoc.field("displayName", String.class);
        to.setDisplayName(toDisplayName);

        impression.setTo(to);
        */

        return impression;
    }

    public static Invitation buildInvitationSelf(ODocument doc) {
        System.out.println("in user mapper: " + doc); 

        Invitation invitation = new Invitation();
        invitation.setId(OrientIdentityUtil.encode(doc.getIdentity().toString()));

        String content = doc.field("content", String.class);
        invitation.setContent(content);

        Date createTime = doc.field("createTime", Date.class);
        invitation.setCreateTime(createTime);

        /**
        User from = new User();
        ODocument fromDoc = invitationDoc.field("out");
        from.setId(OrientIdentityUtil.encode(fromDoc.getIdentity().toString()));

        String fromDisplayName = fromDoc.field("displayName", String.class);
        from.setDisplayName(fromDisplayName);

        invitation.setInvitor(from);

        User to = new User();
        ODocument toDoc = invitationDoc.field("in");
        to.setId(OrientIdentityUtil.encode(toDoc.getIdentity().toString()));

        String toDisplayName = toDoc.field("displayName", String.class);
        to.setDisplayName(toDisplayName);

        invitation.setInvitee(to);
        */

        return invitation;
    }
}
