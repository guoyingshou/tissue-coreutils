package com.tissue.core.mapper;

import com.tissue.core.Account;
import com.tissue.core.About;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.core.command.ContentCommand;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class AboutMapper {

    public static ODocument convertAbout(ContentCommand command) {
        ODocument doc = new ODocument("About");
        doc.field("content", command.getContent());
        return doc;
    }

    public static About buildAbout(ODocument doc) {
        About about = new About();
        about.setId(doc.getIdentity().toString());

        String content = doc.field("content", String.class);
        about.setContent(content);

        Set<ODocument> edgeCreateAboutDocs = doc.field("in");
        for(ODocument edgeCreateAboutDoc : edgeCreateAboutDocs) {
            String category = edgeCreateAboutDoc.field("category", String.class);
            if("praise".equals(category)) {
                Date createTime = edgeCreateAboutDoc.field("createTime", Date.class);
                about.setCreateTime(createTime);

                ODocument accountDoc = edgeCreateAboutDoc.field("out");
                Account account = AccountMapper.buildAccount(accountDoc);
                about.setAccount(account);
                break;
            }
        }
        return about;
    }

}
