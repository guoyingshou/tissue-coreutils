package com.tissue.core.security.dao.orient;

import com.tissue.core.social.User;
import com.tissue.core.mapper.UserDetailsMapper;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.security.UserDetailsImpl;
import com.tissue.core.security.dao.UserDetailsDao;

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

@Component
public class UserDetailsDaoImpl implements UserDetailsDao {


    @Autowired
    private OrientDataSource dataSource;

    public void setDataSource(OrientDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public UserDetailsImpl getUserByUsername(String username) {
        UserDetailsImpl userDetails = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            String sqlUsername = "select from User where username = ?";
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery(sqlUsername);
            List<ODocument> result = db.command(query).execute(username);
            if((result != null) && (result.size() > 0)) {
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
