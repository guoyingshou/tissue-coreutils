package com.tissue.core.mapper;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.social.User;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
/**
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
*/

public class ConnectionMapper {

    public static User.Connection buildConnection(ODocument connectionDoc) {

        User.Connection connection = new User.Connection();
        connection.setId(OrientIdentityUtil.encode(connectionDoc.getIdentity().toString()));

        ODocument fromDoc = connectionDoc.field("out");
        User from = UserMapper.buildUser(fromDoc);
        connection.setFrom(from);

        ODocument toDoc = connectionDoc.field("in");
        User to = UserMapper.buildUser(toDoc);
        connection.setTo(to);

        String status = connectionDoc.field("status");
        connection.setStatus(status);

        String content = connectionDoc.field("content");
        connection.setContent(content);

        Date createTime = connectionDoc.field("createTime", Date.class);
        connection.setCreateTime(createTime);

        return connection;
    }

}
