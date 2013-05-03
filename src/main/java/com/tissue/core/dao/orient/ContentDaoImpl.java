package com.tissue.core.dao.orient;

import com.tissue.core.datasources.OrientDataSource;
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

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

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
        OrientGraph db = dataSource.getDB();
        try {

            OrientVertex v = db.getVertex(command.getId());
            v.setProperty("content", command.getContent());
        }
        finally {
            db.shutdown();
        }
    }

    public void delete(String rid) {
        String sql = "update " + rid + " set deleted = true";
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
