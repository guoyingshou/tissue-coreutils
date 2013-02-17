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

    public String create(PostMessageCommentCommand comment) {
        String id = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument commentDoc = PostMessageCommentMapper.convertPostMessageComment(comment);
            db.save(commentDoc);

            id = commentDoc.getIdentity().toString();
            String msgId = comment.getPostMessage().getId();
            String userId = comment.getUser().getId();

            String sql = "update " + id + " set postMessage = " + msgId;
            //executeCommand(db, sql);
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 

            sql = "create edge EdgePost from " + userId + " to " + id + " set label = 'postMessageComment', createTime = sysdate()";
            //executeCommand(db, sql);
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        
            sql = "update " + msgId + " add comments = " + id;
            //executeCommand(db, sql);
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            //comment.setId(OrientIdentityUtil.encode(ridComment));
        }
        finally {
            db.close();
        }
        return id;
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
