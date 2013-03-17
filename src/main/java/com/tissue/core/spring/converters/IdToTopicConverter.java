package com.tissue.core.spring.converters;

import com.tissue.core.plan.Topic;
import com.tissue.core.plan.dao.TopicDao;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdToTopicConverter implements Converter<String, Topic> {

    @Autowired
    private TopicDao topicDao;

    public Topic convert(String src) {
        return topicDao.getTopic("#"+src);
    }
}
