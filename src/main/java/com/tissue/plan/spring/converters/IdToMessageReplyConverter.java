package com.tissue.plan.spring.converters;

import com.tissue.plan.MessageReply;
import com.tissue.plan.dao.MessageReplyDao;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdToMessageReplyConverter implements Converter<String, MessageReply> {

    @Autowired
    private MessageReplyDao messageReplyDao;

    public MessageReply convert(String src) {
        return messageReplyDao.getMessageReply("#"+src);
    }
}
