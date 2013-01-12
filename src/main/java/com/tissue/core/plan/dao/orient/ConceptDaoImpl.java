package com.tissue.core.plan.dao.orient;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.mapper.PostMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Concept;
import com.tissue.core.plan.dao.ConceptDao;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.nio.charset.Charset;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.tx.OTransaction;

@Component
public class ConceptDaoImpl implements ConceptDao {

    @Autowired
    private OrientDataSource dataSource;

    public Concept create(Concept concept) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = PostMapper.convert(concept);
            doc.save();

            String ridConcept = doc.getIdentity().toString();
            String ridUser = OrientIdentityUtil.decode(concept.getUser().getId());

            String sql = "create edge EdgeConcept from " + 
                          ridUser + 
                          " to " + 
                          ridConcept + 
                          " set label = 'concept', createTime = sysdate()";

            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            String conceptId = OrientIdentityUtil.encode(ridConcept);
            concept.setId(conceptId);
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return concept;
    }

}
