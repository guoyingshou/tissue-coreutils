package com.tissue.social.dao.orient;

import com.tissue.core.datasources.OrientDataSource;
import com.tissue.core.mapper.UserMapper;
import com.tissue.social.mapper.ImpressionMapper;
import com.tissue.social.command.ImpressionCommand;
import com.tissue.social.Impression;
import com.tissue.social.dao.ImpressionDao;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.nio.charset.Charset;
import com.google.common.hash.Hashing;

@Repository
public class ImpressionDaoImpl implements ImpressionDao {

    private static Logger logger = LoggerFactory.getLogger(ImpressionDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public void create(ImpressionCommand command) {
        String accountId = command.getAccount().getId();
        String userId = command.getTo().getId();
        String content = command.getContent();

        String sql = "create edge Impressions " +
                     "from " + accountId + 
                     " to " + userId + 
                     " set category = 'impression', createTime = sysdate(), content = '" + content + "'";
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

    public void update(ImpressionCommand command) {
        OrientGraph db = dataSource.getDB();
        try {
            OrientVertex v = db.getVertex(command.getId());
            v.setProperty("content", command.getContent());
        }
        finally {
            db.shutdown();
        }
    }

    public Impression getImpression(String impressionId) {
        String sql = "select from " + impressionId;
        logger.debug(sql);

        Impression impression = null;

        /**
        OrientGraph db = dataSource.getDB();
        try {
            List<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                impression = ImpressionMapper.buildImpression(doc);
            }
        }
        finally {
            db.shutdown();
        }
        */
        return impression;
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

    public List<Impression> getImpressions(String userId) {
        String sql = "select from Impressions where in in " + userId;
        logger.debug(sql);

        List<Impression> impressions = new ArrayList();

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.getRawGraph().command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            for(ODocument doc : docs) {
                Impression impression = ImpressionMapper.buildImpression(doc);
                impressions.add(impression);
            }
        }
        finally {
            db.shutdown();
        }
        return impressions;
    }

}
