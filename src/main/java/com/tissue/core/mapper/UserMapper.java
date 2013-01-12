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

    public static User buildUser(ODocument userDoc) {
        User user = new User();
        user.setId(OrientIdentityUtil.encode(userDoc.getIdentity().toString()));

        String displayName = userDoc.field("displayName", String.class);
        user.setDisplayName(displayName);

        String resume = userDoc.field("resume", String.class);
        user.setResume(resume);

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
