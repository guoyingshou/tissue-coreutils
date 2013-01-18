package com.tissue.core.plan.dao.orient;

import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.mapper.AnswerCommentMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Answer;
import com.tissue.core.plan.AnswerComment;
import com.tissue.core.plan.dao.AnswerCommentDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;

@Component
public class AnswerCommentDaoImpl extends OrientDao implements AnswerCommentDao {

    public AnswerComment create(AnswerComment comment) {

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument commentDoc = AnswerCommentMapper.convertAnswerComment(comment);
            saveDoc(commentDoc);

            String ridComment = commentDoc.getIdentity().toString();
            String ridUser = OrientIdentityUtil.decode(comment.getUser().getId());
            String ridAnswer = OrientIdentityUtil.decode(comment.getAnswer().getId());

            String sql = "create edge from " + ridUser + " to " + ridComment + " set label = 'answerComment', createTime = sysdate()";
            executeCommand(db, sql);

            sql = "update " + ridAnswer + " add comments = " + ridComment;
            executeCommand(db, sql);
 
            comment.setId(OrientIdentityUtil.encode(ridComment));
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
             db.close();
        }

        return comment;
    }

    public void update(AnswerComment comment) {
        OGraphDatabase db = dataSource.getDB();
        try {
            String ridComment = OrientIdentityUtil.decode(comment.getId());
            String sql = "update " + ridComment + " set content = '" + comment.getContent() + "'";
            executeCommand(db, sql);
        }
        finally {
            db.close();
        }
    }

    public void delete(String commentId) {
        OGraphDatabase db = dataSource.getDB();

        try {
            String ridComment = OrientIdentityUtil.decode(commentId);
            String sql = "update " + ridComment + " set status = 'deleted'";
            executeCommand(db, sql);
        }
        finally {
            db.close();
        }
    }

}
