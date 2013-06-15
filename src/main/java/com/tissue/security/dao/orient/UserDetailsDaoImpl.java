package com.tissue.security.dao.orient;

import com.tissue.core.datasources.OrientDataSource;
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
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.record.impl.ODocument;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import com.tinkerpop.blueprints.Vertex;

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

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<Vertex> docs = db.command(new OSQLSynchQuery(sql)).execute(username);
            for(Vertex doc : docs) {
                userDetails = UserDetailsMapper.buildUser(doc);
                break;
            }
        }
        finally {
            db.shutdown();
        }

        return userDetails;
    }

}
