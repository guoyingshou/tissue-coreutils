package com.tissue.core.spring.converters;

import com.tissue.core.User;
import com.tissue.core.dao.UserDao;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
public class IdToUserConverter implements Converter<String, User> {

    @Autowired
    private UserDao userDao;

    public User convert(String src) {
        return userDao.getUser("#"+src);
    }
}
