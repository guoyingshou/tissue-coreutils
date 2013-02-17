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

    public String create(AnswerCommand answer) {

        String id = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = AnswerMapper.convertAnswer(answer);
            db.save(doc);

            id = doc.getIdentity().toString();
            String userId = answer.getUser().getId();
            String qId = answer.getQuestion().getId();

            String sql = "create edge EdgePost from " + userId + " to " + id+ " set label = 'answer', createTime = sysdate()";
            //executeCommand(db, sql);
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + id + " set question = " + qId;
            //executeCommand(db, sql);
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + qId + " add answers = " + id;
            //executeCommand(db, sql);
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            //answer.setId(OrientIdentityUtil.encode(ridAnswer));

        }
        finally {
            db.close();
        }
        return id;
    }

    public void update(AnswerCommand answer) {
        OGraphDatabase db = dataSource.getDB();
        try {
            //String ridAnswer = OrientIdentityUtil.decode(answer.getId());
            String sql = "update " + answer.getId() + " set content = '" + answer.getContent() + "'";
            //executeCommand(db, sql);
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
            //String ridAnswer = OrientIdentityUtil.decode(answerId);
            String sql = "update " + answerId + " set status = 'deleted'";
            //executeCommand(db, sql);
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
    }

}
