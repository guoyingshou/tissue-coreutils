package com.tissue.core.plan.dao.orient;

import com.tissue.core.command.AnswerCommand;
import com.tissue.core.util.OrientDataSource;

import com.tissue.core.mapper.AnswerMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Post;
import com.tissue.core.plan.Answer;
import com.tissue.core.plan.dao.AnswerDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

@Component
public class AnswerDaoImpl implements AnswerDao {
    @Autowired
    protected OrientDataSource dataSource;

    public Answer create(AnswerCommand command) {

        Answer answer = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = AnswerMapper.convertAnswer(command);
            db.save(doc);

            String id = doc.getIdentity().toString();
            String userId = command.getUser().getId();
            String qId = command.getQuestion().getId();

            String sql = "create edge EdgePost from " + userId + " to " + id+ " set label = 'answer', createTime = sysdate()";
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + id + " set question = " + qId;
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + qId + " add answers = " + id;
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            answer = new Answer();
            answer.setId(id);
            answer.setContent(command.getContent());
            answer.setUser(command.getUser());
            answer.setQuestion(command.getQuestion());
        }
        finally {
            db.close();
        }
        return answer;
    }

    public void update(AnswerCommand command) {
        OGraphDatabase db = dataSource.getDB();
        try {
            String sql = "update " + command.getId() + " set content = '" + command.getContent() + "'";
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
         }
        finally {
            db.close();
        }
    }

    public void delete(String answerId) {
        OGraphDatabase db = dataSource.getDB();
        try {
            String sql = "update " + answerId + " set status = 'deleted'";
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
    }

}
