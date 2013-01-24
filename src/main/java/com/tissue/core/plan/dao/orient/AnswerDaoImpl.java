package com.tissue.core.plan.dao.orient;

import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.mapper.AnswerMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Post;
import com.tissue.core.plan.Answer;
import com.tissue.core.plan.dao.AnswerDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;

@Component
public class AnswerDaoImpl extends OrientDao implements AnswerDao {

    public Answer create(Answer answer) {

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = AnswerMapper.convertAnswer(answer);
            saveDoc(doc);

            System.out.println("create in answer dao: " + doc);

            String ridAnswer = doc.getIdentity().toString();
            String ridUser = OrientIdentityUtil.decode(answer.getUser().getId());
            String ridQuestion = OrientIdentityUtil.decode(answer.getQuestion().getId());

            String sql = "create edge from " + ridUser + " to " + ridAnswer + " set label = 'answer', createTime = sysdate()";
            executeCommand(db, sql);

            sql = "update " + ridAnswer + " set question = " + ridQuestion;
            executeCommand(db, sql);

            sql = "update " + ridQuestion + " add answers = " + ridAnswer;
            executeCommand(db, sql);
 
            answer.setId(OrientIdentityUtil.encode(ridAnswer));
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return answer;
    }

    public void update(Answer answer) {
        OGraphDatabase db = dataSource.getDB();
        try {
            String ridAnswer = OrientIdentityUtil.decode(answer.getId());
            String sql = "update " + ridAnswer + " set content = '" + answer.getContent() + "'";
            executeCommand(db, sql);
        }
        finally {
            db.close();
        }
    }

    public void delete(String answerId) {
        OGraphDatabase db = dataSource.getDB();
        try {
            String ridAnswer = OrientIdentityUtil.decode(answerId);
            String sql = "update " + ridAnswer + " set status = 'deleted'";
            executeCommand(db, sql);
        }
        finally {
            db.close();
        }
    }

}
