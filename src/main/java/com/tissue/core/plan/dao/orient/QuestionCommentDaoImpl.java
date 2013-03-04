package com.tissue.core.plan.dao.orient;

import com.tissue.core.command.QuestionCommentCommand;
import com.tissue.core.util.OrientDataSource;

import com.tissue.core.mapper.QuestionCommentMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Post;
import com.tissue.core.plan.QuestionComment;
import com.tissue.core.plan.dao.QuestionCommentDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

@Component
public class QuestionCommentDaoImpl implements QuestionCommentDao {
    @Autowired
    protected OrientDataSource dataSource;

    public QuestionComment create(QuestionCommentCommand command) {
        QuestionComment comment = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = QuestionCommentMapper.convertQuestionComment(command);
            db.save(doc);
        
            String id = doc.getIdentity().toString();
            String userId = command.getAccount().getId();
            String qId = command.getQuestion().getId();

            String sql = "create edge EdgePost from " + userId + " to " + id + " set label = 'questionComment', createTime = sysdate()";
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + qId + " add comments = " + id;
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            comment = new QuestionComment();
            comment.setId(id);
            comment.setContent(command.getContent());
            comment.setAccount(command.getAccount());
            comment.setQuestion(command.getQuestion());
        }
        finally {
            db.close();
        }
        return comment;
    }

    public void update(QuestionCommentCommand command) {
        String sql = "update " + command.getId() + " set content = '" + command.getContent() + "'"; 

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
         }
        finally {
            db.close();
        }
    }

    /**
    public void delete(String commentId) {
        String sql = "update " + commentId + " set status = 'deleted'";

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
    }
    */

}
