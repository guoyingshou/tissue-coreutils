package com.tissue.core.social.dao.orient;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.orient.dao.OrientDao;
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

/**
//import com.orientechnologies.orient.core.db.record.OIdentifiable;
//import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;
*/

@Component
public class AboutDaoImpl extends OrientDao implements AboutDao {

    public About create(About about) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = AboutMapper.convertAbout(about);
            saveDoc(doc);

            String ridAbout = doc.getIdentity().toString();
            String ridUser = OrientIdentityUtil.decode(about.getUser().getId());

            String sql = "create edge from " + ridUser + " to " + ridAbout + " set label = 'praise', createTime = sysdate()";

            executeCommand(db, sql);
            about.setId(OrientIdentityUtil.encode(ridAbout));
        }
        catch(Exception exc) {
            //todo
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return about;
    }

    public List<About> getAbouts() {
        List<About> abouts = new ArrayList();

        String sql = "select in.out as user, content, createTime from about";
        OGraphDatabase db = dataSource.getDB();
        try {

            List<ODocument> docs = query(db, sql);
            abouts = AboutMapper.buildAbouts(docs);
        }
        catch(Exception exc) {
            //todo
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return abouts;
    }

}
