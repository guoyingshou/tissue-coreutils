package com.tissue.core.dao.orient;

import com.tissue.core.util.OrientDataSource;
import com.tissue.core.command.ResetRequestCommand;
import com.tissue.core.command.ResetPasswordCommand;
import com.tissue.core.dao.ResetDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.Date;
import java.util.List;
import java.nio.charset.Charset;
import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ResetDaoImpl implements ResetDao {

    private static Logger logger = LoggerFactory.getLogger(ResetDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public String create(ResetRequestCommand command) {
        OGraphDatabase db = dataSource.getDB();
        String resetId = null;
        try {
            ODocument doc = new ODocument("Reset");
            doc.field("code", command.getCode());
            doc.field("email", command.getEmail());
            doc.field("createTime", new Date());
            doc.save();

            resetId = doc.getIdentity().toString();
        }
        finally {
            db.close();
        }
        return resetId;
    }

    public boolean isEmailExist(String email) {
        String sql = "select from Account where email = '" + email + "'";
        logger.debug(sql);

        boolean exist = false;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql));
            if(!docs.isEmpty()) {
                exist = true;
            }
        }
        finally {
            db.close();
        }
        return exist;
    }

    public boolean isCodeExist(String code) {
        String sql = "select from Reset where code = '" + code + "'";
        logger.debug(sql);

        boolean exist = false;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql));
            if(!docs.isEmpty()) {
                exist = true;
            }
        }
        finally {
            db.close();
        }
        return exist;
    }

    public String getEmail(String code) {
        String sql = "select email from Reset where code = '" + code + "'";
        logger.debug(sql);

        String email = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                email = doc.field("email", String.class);
            }
        }
        finally {
            db.close();
        }
        return email;
    }

    public void updatePassword(ResetPasswordCommand command) {
        String sql = "select email from Reset where code = '" + command.getCode() + "'";
        logger.debug(sql);

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                String email = doc.field("email", String.class);
                String password = Hashing.md5().hashString(command.getPassword(), Charset.forName("utf-8")).toString();

                sql = "update account set password = '" + password + "' where email = '" + email + "'";
                logger.debug(sql);
                OCommandSQL cmd = new OCommandSQL(sql);
                db.command(cmd).execute();

                sql = "delete from Reset where email = '" + email + "'";
                logger.debug(sql);
                cmd = new OCommandSQL(sql);
                db.command(cmd).execute();
            }
        }
        finally {
            db.close();
        }
    }

}
