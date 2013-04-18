package com.tissue.core.dao.orient;

import com.tissue.core.Reset;
import com.tissue.core.datasources.OrientDataSource;
import com.tissue.core.command.ResetCommand;
import com.tissue.core.dao.ResetDao;
import com.tissue.core.mapper.ResetMapper;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class ResetDaoImpl implements ResetDao {

    private static Logger logger = LoggerFactory.getLogger(ResetDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public String create(ResetCommand command) {
        OGraphDatabase db = dataSource.getDB();
        String resetId = null;
        try {
            ODocument doc = new ODocument("Reset");
            doc.field("code", command.getCode());
            doc.field("account", new ORecordId(command.getAccount().getId()));
            doc.field("createTime", new Date());
            doc.save();

            resetId = doc.getIdentity().toString();
        }
        finally {
            db.close();
        }
        return resetId;
    }

    public Reset getReset(String code) {
        String sql = "select from Reset where code = '" + code + "'";
        logger.debug(sql);

        Reset reset = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                reset = ResetMapper.buildReset(doc);
            }
        }
        finally {
            db.close();
        }
        return reset;
    }

    public void delete(String resetId) {
        String sql = "delete from " + resetId;
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
