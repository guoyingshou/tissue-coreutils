package com.tissue.plan.spring.converters;

import com.tissue.plan.Message;
import com.tissue.plan.dao.MessageDao;

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
