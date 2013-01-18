package com.tissue.core.plan.dao.orient;

import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.mapper.QuestionCommentMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Post;
import com.tissue.core.plan.QuestionComment;
import com.tissue.core.plan.dao.QuestionCommentDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;

@Component
public class QuestionCommentDaoImpl extends OrientDao implements QuestionCommentDao {

    public QuestionComment create(QuestionComment comment) {

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument commentDoc = QuestionCommentMapper.convertQuestionComment(comment);
            saveDoc(commentDoc);
        
            String ridComment = commentDoc.getIdentity().toString();
            String ridUser = OrientIdentityUtil.decode(comment.getUser().getId());
            String ridQuestion = OrientIdentityUtil.decode(comment.getQuestion().getId());

            String sql = "create edge from " + ridUser + " to " + ridComment + " set label = 'questionComment', createTime = sysdate()";
            executeCommand(db, sql);

            sql = "update " + ridQuestion + " add comments = " + ridComment;
            executeCommand(db, sql);

            comment.setId(OrientIdentityUtil.encode(ridComment));
        }
        finally {
            db.close();
        }
        return comment;
    }

    public void update(QuestionComment comment) {
        String ridComment = OrientIdentityUtil.decode(comment.getId());
        String sql = "update " + ridComment + " set content = '" + comment.getContent() + "'"; 

        OGraphDatabase db = dataSource.getDB();
        try {
            executeCommand(db, sql);
        }
        finally {
            db.close();
        }
    }

    public void delete(String commentId) {
        String ridComment = OrientIdentityUtil.decode(commentId);
        String sql = "update " + ridComment + " set status = 'deleted'";

        OGraphDatabase db = dataSource.getDB();
        try {
            executeCommand(db, sql);
        }
        finally {
            db.close();
        }
    }

}
