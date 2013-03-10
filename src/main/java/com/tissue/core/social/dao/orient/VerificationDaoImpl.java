package com.tissue.core.social.dao.orient;

import com.tissue.core.util.OrientDataSource;
import com.tissue.core.command.VerificationCommand;
import com.tissue.core.social.dao.VerificationDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

@Component
public class VerificationDaoImpl implements VerificationDao {

    private static Logger logger = LoggerFactory.getLogger(VerificationDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public String create(VerificationCommand command) {
        OGraphDatabase db = dataSource.getDB();
        String verificationId = null;
        try {
            ODocument doc = new ODocument("Verification");
            doc.field("code", command.getCode());
            doc.field("account", new ORecordId(command.getAccount().getId()));
            doc.field("createTime", new Date());
            doc.save();

            verificationId = doc.getIdentity().toString();
        }
        finally {
            db.close();
        }
        return verificationId;
    }

    public String getAccountId(String code) {
        String sql = "select account from Verification where code = '" + code + "'";
        logger.debug(sql);

        String accountId = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                ODocument accountDoc = doc.field("account");
                accountId = accountDoc.getIdentity().toString();
            }
        }
        finally {
            db.close();
        }
        return accountId;
    }

    public void setVerified(String accountId) {
        String sql = "delete from Verification where account in " + accountId;
        logger.debug(sql);

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            sql = "update " + accountId + " set verified = true";
            logger.debug(sql);

            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
    }


}
