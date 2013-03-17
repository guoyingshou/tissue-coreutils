package com.tissue.core.spring.converters;

import com.tissue.core.plan.Message;
import com.tissue.core.plan.dao.MessageDao;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdToMessageConverter implements Converter<String, Message> {

    @Autowired
    private MessageDao messageDao;

    public Message convert(String src) {
        return messageDao.getMessage("#"+src);
    }
}
