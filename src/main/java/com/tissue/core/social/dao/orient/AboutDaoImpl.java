package com.tissue.core.social.dao.orient;

import com.tissue.core.command.AboutCommand;
import com.tissue.core.exceptions.NoRecordFoundException;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.mapper.AboutMapper;
import com.tissue.core.social.About;
import com.tissue.core.social.dao.AboutDao;

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
public class AboutDaoImpl implements AboutDao {

    private static Logger logger = LoggerFactory.getLogger(AboutDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public String addAbout(AboutCommand command) {
        String id = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = AboutMapper.convertAbout(command);
            doc.save();

            id = doc.getIdentity().toString();
            String sql = "create edge EdgePost from " + command.getAccount().getId() + " to " + id + " set label = 'praise', createTime = sysdate()";
            logger.debug(sql);

            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
        return id;
    }

    public List<About> getAbouts() {
        String sql = "select in.out as user, content, createTime from about";
        logger.debug(sql);

        List<About> abouts = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            abouts = AboutMapper.buildAbouts(docs);
        }
        finally {
            db.close();
        }
        return abouts;
    }

}
