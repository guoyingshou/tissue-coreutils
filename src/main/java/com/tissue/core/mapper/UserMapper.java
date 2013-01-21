package com.tissue.core.mapper;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.social.User;
import com.tissue.core.social.Invitation;
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

    public static User buildUserDetails(ODocument doc) {

        User user = null;
        ODocument selfDoc = doc.field("user");
        if(selfDoc != null) {
            user = buildUserSelf(selfDoc);

            List<ODocument> friendsDoc = doc.field("friends", List.class);
            if(friendsDoc != null) {
                for(ODocument friendDoc : friendsDoc) {
                    User friend = buildUserSelf(friendDoc);
                    user.addFriend(friend);
                }
            }

            List<ODocument> declinedsDoc = doc.field("mutualDeclined", List.class);
            if(declinedsDoc != null) {
                for(ODocument declinedDoc : declinedsDoc) {
                    User declined = buildUserSelf(declinedDoc);
                    user.addDeclinedUser(declined);
                }
            }

            List<ODocument> invitationsSentDoc = doc.field("invitationsSent", List.class);
            if(invitationsSentDoc != null) {
                for(ODocument invitationSentDoc : invitationsSentDoc) {
                    Invitation invitation = new Invitation();
                    invitation.setId(OrientIdentityUtil.encode(invitationSentDoc.getIdentity().toString()));

                    /**
                    Date createTime = invitationSentDoc.field("createTime", Date.class);
                    invitation.setCreateTime(createTime);
                    */

                    invitation.setInvitor(user);

                    ODocument inviteeDoc = invitationSentDoc.field("in");
                    User invitee = buildUserSelf(inviteeDoc);
                    invitation.setInvitee(invitee);

                    user.addInvitationSent(invitation);
                }
            }

            List<ODocument> invitationsReceivedDoc = doc.field("invitationsReceived", List.class);
            if(invitationsReceivedDoc != null) {
                for(ODocument invitationReceivedDoc : invitationsReceivedDoc) {
                    Invitation invitation = new Invitation();
                    invitation.setId(OrientIdentityUtil.encode(invitationReceivedDoc.getIdentity().toString()));

                    String content = invitationReceivedDoc.field("content", String.class);
                    invitation.setContent(content);

                    Date createTime = invitationReceivedDoc.field("createTime", Date.class);
                    invitation.setCreateTime(createTime);

                    ODocument invitorDoc = invitationReceivedDoc.field("out");
                    User invitor = buildUserSelf(invitorDoc);
                    invitation.setInvitor(invitor);

                    invitation.setInvitee(user);

                    user.addInvitationReceived(invitation);
                }
            }

         }

        return user;
    }

    public static List<User> buildMembers(Set<ODocument> membersDoc) {
        List<User> members = new ArrayList();

        for(ODocument memberDoc : membersDoc) {
            User user = buildUserSelf(memberDoc);
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
