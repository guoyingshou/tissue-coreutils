package com.tissue.core.plan.dao.orient;

import com.tissue.core.command.PostMessageCommentCommand;
import com.tissue.core.util.OrientDataSource;

import com.tissue.core.mapper.PostMessageCommentMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.PostMessage;
import com.tissue.core.plan.PostMessageComment;
import com.tissue.core.plan.dao.PostMessageCommentDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;


@Component
public class PostMessageCommentDaoImpl implements PostMessageCommentDao {
    @Autowired
    protected OrientDataSource dataSource;

    public PostMessageComment create(PostMessageCommentCommand command) {
        PostMessageComment comment = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = PostMessageCommentMapper.convertPostMessageComment(command);
            db.save(doc);

            String id = doc.getIdentity().toString();
            String msgId = command.getPostMessage().getId();
            String userId = command.getAccount().getId();

            String sql = "update " + id + " set postMessage = " + msgId;
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "create edge EdgePost from " + userId + " to " + id + " set label = 'postMessageComment', createTime = sysdate()";
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        
            sql = "update " + msgId + " add comments = " + id;
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            comment = new PostMessageComment();
            comment.setId(id);
            comment.setContent(command.getContent());
            comment.setAccount(command.getAccount());
            comment.setPostMessage(command.getPostMessage());
 
        }
        finally {
            db.close();
        }
        return comment;
    }

    public void update(PostMessageCommentCommand comment) {
        //String ridComment = OrientIdentityUtil.decode(comment.getId());
        String sql = "update " + comment.getId() + " set content = '" + comment.getContent() + "'";

        OGraphDatabase db = dataSource.getDB();
        try {
            //executeCommand(db, sql);
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
         }
        finally {
           db.close();
        }
    }

    public void delete(String commentId) {
        //String ridComment = OrientIdentityUtil.decode(commentId);
        String sql = "update " + commentId + " set status = 'deleted'";

        OGraphDatabase db = dataSource.getDB();
        try {
            //executeCommand(db, sql);
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
        }
        finally {
            db.close();
        }
    }
}
