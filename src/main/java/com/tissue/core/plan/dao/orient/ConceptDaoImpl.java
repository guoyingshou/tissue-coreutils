package com.tissue.core.plan.dao.orient;

import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.mapper.PostMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Concept;
import com.tissue.core.plan.dao.ConceptDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;

@Component
public class ConceptDaoImpl extends OrientDao implements ConceptDao {

    public Concept create(Concept concept) {

        OGraphDatabase db = dataSource.getDB();

        try {
        ODocument doc = PostMapper.convert(concept);
        saveDoc(doc);

        String ridConcept = doc.getIdentity().toString();
        String ridUser = OrientIdentityUtil.decode(concept.getUser().getId());

        String sql = "create edge EdgeConcept from " + ridUser + " to " + ridConcept + " set label = 'concept', createTime = sysdate()";

        executeCommand(db, sql);
        concept.setId(OrientIdentityUtil.encode(ridConcept));
        return concept;
        }
        finally {
            db.close();
        }
    }

}
