package com.tissue.core.social.dao.orient;

import com.tissue.core.util.OrientDataSource;
import com.tissue.core.mapper.UserMapper;
import com.tissue.core.social.command.ImpressionCommand;
import com.tissue.core.social.Impression;
import com.tissue.core.social.dao.ImpressionDao;

import org.springframework.stereotype.Component;
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

@Component
public class ImpressionDaoImpl implements ImpressionDao {

    private static Logger logger = LoggerFactory.getLogger(ImpressionDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public void create(ImpressionCommand command) {
        String sql = "create edge EdgeImpression from " + command.getAccount().getId() + " to " + command.getTo().getId() + " set label = 'impression', createTime = sysdate(), content = '" + command.getContent() + "'";
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
        String sql = "select @this as impression, out as user from EdgeImpression where in in " + userId;
        logger.debug(sql);

        List<Impression> impressions = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                ODocument impDoc = doc.field("impression");
                Impression impression = UserMapper.buildImpressionSelf(impDoc);

                impressions.add(impression);
            }
        }
        finally {
            db.close();
        }
        return impressions;
    }

}
