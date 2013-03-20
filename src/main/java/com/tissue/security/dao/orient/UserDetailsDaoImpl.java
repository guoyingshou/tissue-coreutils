package com.tissue.security.dao.orient;

import com.tissue.core.util.OrientDataSource;
import com.tissue.security.UserDetailsImpl;
import com.tissue.security.mapper.UserDetailsMapper;
import com.tissue.security.dao.UserDetailsDao;

import java.util.List;
import java.util.Date;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Arrays;
import java.nio.charset.Charset;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class UserDetailsDaoImpl implements UserDetailsDao {

    private Logger logger = LoggerFactory.getLogger(UserDetailsDaoImpl.class);

    @Autowired
    private OrientDataSource dataSource;

    public void setDataSource(OrientDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public UserDetailsImpl getUserByUsername(String username) {
        String sql = "select from Account where username = '" + username + "' or email = '" + username + "'";
        logger.debug(sql);

        UserDetailsImpl userDetails = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery(sql);
            List<ODocument> result = db.command(query).execute(username);
            if(!result.isEmpty()) {
                ODocument doc = result.get(0);
                userDetails = UserDetailsMapper.buildUser(doc);
            }
        }
        finally {
            db.close();
        }

        return userDetails;
    }

}
