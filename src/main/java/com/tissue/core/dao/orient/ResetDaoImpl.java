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

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

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
        String resetId = null;

        OrientGraph db = dataSource.getDB();
        try {

            ODocument doc = new ODocument("Reset");
            doc.field("code", command.getCode());
            doc.field("account", new ORecordId(command.getAccount().getId()));
            doc.field("createTime", new Date());
            doc.save();

            resetId = doc.getIdentity().toString();
        }
        finally {
            db.shutdown();
        }
        return resetId;
    }

    public Reset getReset(String code) {
        String sql = "select from Reset where code = '" + code + "'";
        logger.debug(sql);

        Reset reset = null;

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.getRawGraph().command(new OSQLSynchQuery(sql)).execute();
            for(ODocument doc : docs) {
                reset = ResetMapper.buildReset(doc);
            }
        }
        finally {
            db.shutdown();
        }
        return reset;
    }

    public void delete(String resetId) {
        String sql = "delete from " + resetId;
        logger.debug(sql);

        OrientGraph db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.shutdown();
        }
    }

}
