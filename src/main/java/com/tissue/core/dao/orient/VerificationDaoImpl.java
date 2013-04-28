package com.tissue.core.dao.orient;

import com.tissue.core.datasources.OrientDataSource;
import com.tissue.core.Verification;
import com.tissue.core.dao.VerificationDao;
import com.tissue.core.command.VerificationCommand;
import com.tissue.core.mapper.VerificationMapper;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

@Repository
public class VerificationDaoImpl implements VerificationDao {

    private static Logger logger = LoggerFactory.getLogger(VerificationDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public String create(VerificationCommand command) {
        String verificationId = null;

        //OGraphDatabase db = dataSource.getDB();
        OrientGraph db = dataSource.getDB();
        try {
            
            ODocument doc = new ODocument("Verification");
            doc.field("code", command.getCode());
            doc.field("account", new ORecordId(command.getAccount().getId()));
            doc.field("createTime", new Date());
            doc.save();

            verificationId = doc.getIdentity().toString();
        }
        finally {
            //db.close();
            db.shutdown();
        }
        return verificationId;
    }

    public Verification getVerification(String code) {
        String sql = "select from Verification where code = '" + code + "'";
        logger.debug(sql);

        Verification verification = null;

        //OGraphDatabase db = dataSource.getDB();
        OrientGraph db = dataSource.getDB();
        try {
            //List<ODocument> docs = db.query(new OSQLSynchQuery(sql));
            
            OCommandSQL cmd = new OCommandSQL(sql);
            List<ODocument> docs = db.command(cmd).execute();

            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                verification = VerificationMapper.buildVerification(doc);
            }
        }
        finally {
            //db.close();
            db.shutdown();
        }
        return verification;
    }

    public void delete(String verificationId) {
        String sql = "delete from " + verificationId;
        logger.debug(sql);

        //OGraphDatabase db = dataSource.getDB();
        OrientGraph db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            //db.close();
            db.shutdown();
        }
    }

}
