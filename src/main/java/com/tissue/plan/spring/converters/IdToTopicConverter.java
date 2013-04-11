package com.tissue.plan.spring.converters;

import com.tissue.plan.Topic;
import com.tissue.plan.dao.TopicDao;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;

//@Component
public class IdToTopicConverter implements Converter<String, Topic> {

    @Autowired
    private TopicDao topicDao;

    public Topic convert(String src) {
        return topicDao.getTopic("#"+src);
    }
}
