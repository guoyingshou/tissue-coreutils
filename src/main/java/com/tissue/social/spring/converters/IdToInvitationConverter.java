package com.tissue.social.spring.converters;

import com.tissue.social.Invitation;
import com.tissue.social.dao.InvitationDao;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdToInvitationConverter implements Converter<String, Invitation> {

    @Autowired
    private InvitationDao invitationDao;

    public Invitation convert(String src) {
        return invitationDao.getInvitation("#"+src);
    }
}
