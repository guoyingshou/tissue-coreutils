package com.tissue.social.dao.orient;

import com.tissue.core.util.OrientDataSource;
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
        String sql = "create edge EdgeCreate from " + command.getAccount().getId() + " to " + command.getTo().getId() + " set label = 'impression', createTime = sysdate(), content = '" + command.getContent() + "'";
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

    public Impression getImpression(String impressionId) {
        String sql = "select from " + impressionId;
        logger.debug(sql);

        Impression impression = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                impression = ImpressionMapper.buildImpression(doc);
            }
        }
        finally {
            db.close();
        }
        return impression;
    }

    public void update(ImpressionCommand command) {
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

    public List<Impression> getImpressions(String userId) {
        String sql = "select from EdgeCreate where label = 'impression' and in in " + userId;
        logger.debug(sql);

        List<Impression> impressions = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                Impression impression = ImpressionMapper.buildImpression(doc);

                impressions.add(impression);
            }
        }
        finally {
            db.close();
        }
        return impressions;
    }

}
