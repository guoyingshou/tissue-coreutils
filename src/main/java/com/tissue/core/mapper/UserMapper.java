package com.tissue.core.mapper;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.social.User;
import com.tissue.core.social.Impression;

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

    public static User buildUser(ODocument userDoc) {
        return buildUser(userDoc, false);
    }

    public static User buildUser(ODocument userDoc, boolean hasRid) {
        User user = new User();

        String rid = null;
        if(hasRid) {
            rid = userDoc.field("rid", String.class);
        }
        else {
            rid = userDoc.getIdentity().toString();
        }
        user.setId(OrientIdentityUtil.encode(rid));

        String displayName = userDoc.field("displayName", String.class);
        user.setDisplayName(displayName);

        String resume = userDoc.field("resume", String.class);
        user.setResume(resume);

        List<ODocument> connectionsDoc = userDoc.field("connections");
        if(connectionsDoc != null) {
            for(ODocument connDoc : connectionsDoc) {
                User.Connection conn = new User.Connection();
                conn.setId(OrientIdentityUtil.encode(connDoc.getIdentity().toString()));

                ODocument fromDoc = connDoc.field("out");
                User from = buildUser(fromDoc);
                conn.setFrom(from);

                ODocument toDoc = connDoc.field("in");
                User to = buildUser(toDoc);
                conn.setTo(to);

                String status = connDoc.field("status");
                conn.setStatus(status);

                String content = connDoc.field("content");
                conn.setContent(content);

                Date createTime = connDoc.field("createTime", Date.class);
                conn.setCreateTime(createTime);

                user.addConnection(conn);
            }
        }
        return user;
    }

    public static List<User> buildMembers(Set<ODocument> membersDoc) {
        List<User> members = new ArrayList();

        for(ODocument memberDoc : membersDoc) {
            User user = buildUser(memberDoc);
            members.add(user);
        }
        return members;
    }

    public static List<Impression> buildImpressions(List<ODocument> impressionsDoc) {
        List<Impression> impressions = new ArrayList();
        for(ODocument doc : impressionsDoc) {
            impressions.add(buildImpression(doc));
        }
        return impressions;
    }

    public static Impression buildImpression(ODocument impressionDoc) {

        Impression impression = new Impression();
        impression.setId(OrientIdentityUtil.encode(impressionDoc.getIdentity().toString()));

        String content = impressionDoc.field("content", String.class);
        impression.setContent(content);

        Date published = impressionDoc.field("published", Date.class);
        impression.setPublished(published);

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

        return impression;
    }
}
