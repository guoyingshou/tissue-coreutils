package com.tissue.core.plan.dao.orient;

import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.mapper.PostMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Tutorial;
import com.tissue.core.plan.dao.TutorialDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;

@Component
public class TutorialDaoImpl extends OrientDao implements TutorialDao {

    public Tutorial create(Tutorial tutorial) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = PostMapper.convert(tutorial);
            saveDoc(doc);

            String ridTutorial = doc.getIdentity().toString();
            String ridUser = OrientIdentityUtil.decode(tutorial.getUser().getId());
            String ridPlan = OrientIdentityUtil.decode(tutorial.getPlan().getId());

            String sql = "update " + ridTutorial + " set plan = " + ridPlan;
            executeCommand(db, sql);

            sql = "create edge EdgeTutorial from " + ridUser + " to " + ridTutorial + " set label = 'tutorial', createTime = sysdate()";
            executeCommand(db, sql);

            tutorial.setId(OrientIdentityUtil.encode(ridTutorial));
            return tutorial;
        }
        finally {
            db.close();
        }
    }

}
