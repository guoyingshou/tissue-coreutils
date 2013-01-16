package com.tissue.core.plan.dao.orient;

import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.mapper.PostMessageCommentMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.PostMessage;
import com.tissue.core.plan.PostMessageComment;
import com.tissue.core.plan.dao.PostMessageCommentDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;

@Component
public class PostMessageCommentDaoImpl extends OrientDao implements PostMessageCommentDao {

    public PostMessageComment create(PostMessageComment comment) {
        OGraphDatabase db = dataSource.getDB();
        try {

        ODocument commentDoc = PostMessageCommentMapper.convertPostMessageComment(comment);
        saveDoc(commentDoc);

        String ridComment = commentDoc.getIdentity().toString();

        String ridPostMessage = OrientIdentityUtil.decode(comment.getPostMessage().getId());
        String ridUser = OrientIdentityUtil.decode(comment.getUser().getId());

        String sql = "update " + ridComment + " set postMessage = " + ridPostMessage;

        String sql2 = "create edge EdgePostMessageComment from " + ridUser + " to " + ridComment + " set label = 'postMessageComment', createTime = sysdate()";
        
        String sql3 = "update " + ridPostMessage + " add comments = " + ridComment;

        executeCommand(db, sql);
        executeCommand(db, sql2);
        executeCommand(db, sql3);
 
        comment.setId(OrientIdentityUtil.encode(ridComment));
        return comment;
        }
        finally {
            db.close();
        }
    }

    public PostMessageComment update(PostMessageComment comment) {

        String ridComment = OrientIdentityUtil.decode(comment.getId());
        String sql = "update " + ridComment + " set content = '" + comment.getContent() + "'";

        OGraphDatabase db = dataSource.getDB();
        try {
        executeCommand(db, sql);
        return comment;
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
