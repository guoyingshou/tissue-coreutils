package com.tissue.core.mapper;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.social.User;
import com.tissue.core.social.About;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class AboutMapper {

    public static ODocument convertAbout(About about) {
        ODocument doc = new ODocument("About");
        doc.field("content", about.getContent());
        return doc;
    }

    public static List<About> buildAbouts(List<ODocument> docs) {
        List<About> abouts = new ArrayList();
        for(ODocument doc : docs) {
            abouts.add(buildAbout(doc));
        }

        return abouts;
    }

    public static About buildAbout(ODocument doc) {
        About about = new About();
        about.setId(OrientIdentityUtil.encode(doc.getIdentity().toString()));

        String content = doc.field("content", String.class);
        about.setContent(content);

        List<ODocument> docs = doc.field("user");
        ODocument userDoc = docs.get(0);
        User user = UserMapper.buildUser(userDoc);
        about.setUser(user);

        return about;
    }
}
