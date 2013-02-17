package com.tissue.core.social.dao.orient;

//import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.util.OrientDataSource;

import com.tissue.core.mapper.AboutMapper;
import com.tissue.core.social.About;
import com.tissue.core.social.User;
import com.tissue.core.social.dao.AboutDao;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
//import com.orientechnologies.orient.core.db.record.OIdentifiable;
//import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

@Component
public class AboutDaoImpl implements AboutDao {
    @Autowired
    protected OrientDataSource dataSource;

    public String create(About about) {
        String id = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = AboutMapper.convertAbout(about);
            doc.save();

            id = doc.getIdentity().toString();
            //String ridUser = OrientIdentityUtil.decode(about.getUser().getId());

            String sql = "create edge from " + about.getUser().getId() + " to " + id + " set label = 'praise', createTime = sysdate()";

            //executeCommand(db, sql);
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            //about.setId(OrientIdentityUtil.encode(ridAbout));
        }
        finally {
            db.close();
        }
        return id;
    }

    public List<About> getAbouts() {
        List<About> abouts = new ArrayList();

        String sql = "select in.out as user, content, createTime from about";
        OGraphDatabase db = dataSource.getDB();
        try {
            //List<ODocument> docs = query(db, sql);
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            abouts = AboutMapper.buildAbouts(docs);
        }
        finally {
            db.close();
        }
        return abouts;
    }

}
