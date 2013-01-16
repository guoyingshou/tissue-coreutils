package com.tissue.core.orient.dao;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.util.OrientDataSource;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;

public abstract class OrientDao {

    @Autowired
    protected OrientDataSource dataSource;

    protected String saveDoc(ODocument doc) {
        doc.save();
        return OrientIdentityUtil.encode(doc.getIdentity().toString());
    }

    protected List<ODocument> query(OGraphDatabase db, String sql) {
        List<ODocument> result = new ArrayList();
        result = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
        return result;
    }

    protected ODocument querySingle(OGraphDatabase db, String sql) {
        List<ODocument> result = new ArrayList();
        result = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
        return (result != null && result.size() > 0) ? result.get(0) : null;
    }

    protected void executeCommand(OGraphDatabase db, String sql) {
        OCommandSQL cmd = new OCommandSQL(sql);
        db.command(cmd).execute();
    }
     /**
    protected void executeCommand(OGraphDatabase db, String ... sqls) {
        for(String sql : sqls) {
            System.out.println("sql: " + sql);
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
    }
    */

    protected long countClass(String name) {
        long count = 0L;
        OGraphDatabase db = dataSource.getDB();
        try {
            count = db.countClass(name);
        }
        catch(Exception exc) {
            //todo
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return count;
    }

}
