package com.tissue.social.mapper;

//import com.tissue.core.User;
import com.tissue.social.Impression;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class ImpressionMapper {

    public static Impression buildImpression(ODocument impressionDoc) {

        Impression impression = new Impression();
        impression.setId(impressionDoc.getIdentity().toString());

        String content = impressionDoc.field("content", String.class);
        impression.setContent(content);

        Date createTime = impressionDoc.field("createTime", Date.class);
        impression.setCreateTime(createTime);

        return impression;
    }

}
