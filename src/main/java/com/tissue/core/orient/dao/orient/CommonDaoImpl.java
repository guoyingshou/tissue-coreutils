package com.tissue.core.orient.dao.orient;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.orient.dao.CommonDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.List;

@Component
public class CommonDaoImpl implements CommonDao {
    @Autowired
    protected OrientDataSource dataSource;

    public boolean isResourceExist(String resourceId) {
        boolean exist = false;

        String rid = OrientIdentityUtil.decode(resourceId);
        String sql = "select from " + rid;

        OGraphDatabase db = dataSource.getDB();
        try {
            //List<ODocument> docs = query(db, sql);
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

        String ridUser = OrientIdentityUtil.decode(userId);
        String rid = OrientIdentityUtil.decode(resourceId);

        String sql = "select from " + rid + " where in.out in " + ridUser;

        OGraphDatabase db = dataSource.getDB();
        try {
            //List<ODocument> docs = query(db, sql);
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

        String ridUser = OrientIdentityUtil.decode(userId);
        String ridPost = OrientIdentityUtil.decode(postId);

        String sql = "select from " + ridPost + " where plan.in.out contains " + ridUser;

        OGraphDatabase db = dataSource.getDB();
        try {
            //List<ODocument> docs = query(db, sql);
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
}
