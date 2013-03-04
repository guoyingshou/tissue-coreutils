package com.tissue.core.plan.dao.orient;

import com.tissue.core.command.AnswerCommentCommand;
import com.tissue.core.util.OrientDataSource;

import com.tissue.core.mapper.AnswerCommentMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Answer;
import com.tissue.core.plan.AnswerComment;
import com.tissue.core.plan.dao.AnswerCommentDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

@Component
public class AnswerCommentDaoImpl implements AnswerCommentDao {

    @Autowired
    protected OrientDataSource dataSource;

    public AnswerComment create(AnswerCommentCommand command) {
        AnswerComment comment = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = AnswerCommentMapper.convertAnswerComment(command);
            db.save(doc);

            String id = doc.getIdentity().toString();
            String userId = command.getAccount().getId();
            String answerId = command.getAnswer().getId();

            String sql = "create edge EdgePost from " + userId + " to " + id + " set label = 'answerComment', createTime = sysdate()";
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + answerId + " add comments = " + id;
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            comment = new AnswerComment();
            comment.setId(id);
            comment.setContent(command.getContent());
            comment.setAccount(command.getAccount());
            comment.setAnswer(command.getAnswer());
        }
        finally {
             db.close();
        }

        return comment;
    }

    public void update(AnswerCommentCommand command) {
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

    /**
    public void delete(String commentId) {
        OGraphDatabase db = dataSource.getDB();

        try {
            String sql = "update " + commentId + " set status = 'deleted'";
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
         }
        finally {
            db.close();
        }
    }
    */

}
