package com.tissue.core.plan.dao.orient;

import com.tissue.core.command.AnswerCommentCommand;
//import com.tissue.core.util.OrientIdentityUtil;
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

    public String create(AnswerCommentCommand command) {
        String id = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument commentDoc = AnswerCommentMapper.convertAnswerComment(command);
            db.save(commentDoc);

            id = commentDoc.getIdentity().toString();
            String userId = command.getUser().getId();
            String answerId = command.getAnswer().getId();

            String sql = "create edge EdgePost from " + userId + " to " + id + " set label = 'answerComment', createTime = sysdate()";
            //executeCommand(db, sql);
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + answerId + " add comments = " + id;
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
            //executeCommand(db, sql);
 
            //comment.setId(OrientIdentityUtil.encode(ridComment));
        }
        finally {
             db.close();
        }

        return id;
    }

    public void update(AnswerCommentCommand comment) {
        OGraphDatabase db = dataSource.getDB();
        try {
            //String ridComment = OrientIdentityUtil.decode(comment.getId());
            String sql = "update " + comment.getId() + " set content = '" + comment.getContent() + "'";
            //executeCommand(db, sql);
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
         }
        finally {
            db.close();
        }
    }

    public void delete(String commentId) {
        OGraphDatabase db = dataSource.getDB();

        try {
            //String ridComment = OrientIdentityUtil.decode(commentId);
            String sql = "update " + commentId + " set status = 'deleted'";
            //executeCommand(db, sql);
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
         }
        finally {
            db.close();
        }
    }

}
