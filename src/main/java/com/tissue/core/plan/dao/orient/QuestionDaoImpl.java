package com.tissue.core.plan.dao.orient;

import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.mapper.PostMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Question;
import com.tissue.core.plan.dao.QuestionDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;

@Component
public class QuestionDaoImpl extends OrientDao implements QuestionDao {

    public Question create(Question question) {

        OGraphDatabase db = dataSource.getDB();
        try {
        ODocument doc = PostMapper.convert(question);
        saveDoc(doc);

        String ridQuestion = doc.getIdentity().toString();
        String ridUser = OrientIdentityUtil.decode(question.getUser().getId());

        String sql = "create edge EdgeQuestion from " + ridUser + " to " + ridQuestion + " set label = 'question', createTime = sysdate()";

        executeCommand(db, sql);

        question.setId(OrientIdentityUtil.encode(ridQuestion));
        return question;
        }
        finally {
            db.close();
        }
    }

}
