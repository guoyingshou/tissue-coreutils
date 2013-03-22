package com.tissue.core.dao.orient;

import com.tissue.core.About;
import com.tissue.core.dao.AboutDao;
import com.tissue.core.command.ContentCommand;
import com.tissue.core.mapper.AboutMapper;

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

@Component
public class AboutDaoImpl extends ContentDaoImpl implements AboutDao {

    private static Logger logger = LoggerFactory.getLogger(AboutDaoImpl.class);

    public String create(ContentCommand command) {
        String id = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = AboutMapper.convertAbout(command);
            doc.save();

            id = doc.getIdentity().toString();
            String sql = "create edge EdgeCreate from " + command.getAccount().getId() + " to " + id + " set label = 'praise', createTime = sysdate()";
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
        String sql = "select from about";
        logger.debug(sql);

        List<About> abouts = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                About about = AboutMapper.buildAbout(doc);
                abouts.add(about);
            }
        }
        finally {
            db.close();
        }
        return abouts;
    }

}
