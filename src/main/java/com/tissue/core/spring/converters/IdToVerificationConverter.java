package com.tissue.core.spring.converters;

import com.tissue.core.Verification;
import com.tissue.core.dao.VerificationDao;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;

//@Component
public class IdToVerificationConverter implements Converter<String, Verification> {

    @Autowired
    private VerificationDao verificationDao;

    public Verification convert(String src) {
        return verificationDao.getVerification(src);
    }
}
