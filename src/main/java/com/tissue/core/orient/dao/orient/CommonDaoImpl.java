package com.tissue.core.orient.dao.orient;

import com.tissue.core.util.OrientDataSource;
import com.tissue.core.orient.dao.CommonDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CommonDaoImpl implements CommonDao {

    private static Logger logger = LoggerFactory.getLogger(CommonDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public boolean isResourceExist(String resourceId) {
        boolean exist = false;
        String sql = "select from " + resourceId;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(docs.size() > 0) {
                 exist = true;
            }
        }
        finally {
            db.close();
        }
        return exist;
    }


    public boolean isOwner(String userId, String resourceId) {
        boolean owner = false;
        String sql = "select from " + resourceId + " where in.out in " + userId;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(docs.size() > 0) {
                 owner = true;
            }
        }
        finally {
            db.close();
        }
        return owner;
    }

    public boolean isMemberOrOwner(String userId, String postId) {
        boolean memberOrOwner = false;
        String sql = "select from " + postId + " where plan.in.out contains " + userId;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(docs.size() > 0) {
                 memberOrOwner = true;
            }
        }
        finally {
            db.close();
        }
        return memberOrOwner;
    }

    public void delete(String rid) {
        String sql = "update " + rid + " set deleted = true";
        logger.debug(sql);

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
    }
}
