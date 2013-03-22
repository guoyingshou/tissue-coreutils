package com.tissue.core.dao.orient;

import com.tissue.core.util.OrientDataSource;
import com.tissue.core.command.ContentCommand;
import com.tissue.core.dao.ContentDao;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class ContentDaoImpl implements ContentDao {

    private static Logger logger = LoggerFactory.getLogger(ContentDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public void update(ContentCommand command) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = db.load(new ORecordId(command.getId()));
            doc.field("content", command.getContent());
            doc.save();
        }
        finally {
            db.close();
        }
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
