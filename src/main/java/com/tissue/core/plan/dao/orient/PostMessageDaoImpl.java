package com.tissue.core.plan.dao.orient;

import com.tissue.core.command.PostMessageCommand;
import com.tissue.core.util.OrientDataSource;

import com.tissue.core.mapper.PostMessageMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Post;
import com.tissue.core.plan.PostMessage;
import com.tissue.core.plan.dao.PostMessageDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

@Component
public class PostMessageDaoImpl implements PostMessageDao {
    @Autowired
    protected OrientDataSource dataSource;

    public PostMessage create(PostMessageCommand command) {

        PostMessage postMessage = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = PostMessageMapper.convertPostMessage(command);
            db.save(doc);

            String id = doc.getIdentity().toString();
            String postId = command.getPost().getId();
            String userId = command.getAccount().getId();

            String sql = "update " + id + " set post = " + postId;
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "create edge EdgePost from " + userId + " to " + id + " set label = 'postMessage', createTime = sysdate()";
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + postId + " add messages = " + id;
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            postMessage = new PostMessage();
            postMessage.setId(id);
            postMessage.setContent(command.getContent());
            postMessage.setAccount(command.getAccount());
            postMessage.setPost(command.getPost());
        }
        finally {
            db.close();
        }
        return postMessage;
    }

    public void update(PostMessageCommand message) {

        //String ridMessage = OrientIdentityUtil.decode(message.getId());
        String sql = "update " + message.getId() + " set content = '" + message.getContent() + "'";

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

    public void delete(String messageId) {
        //String ridMessage = OrientIdentityUtil.decode(messageId);
        String sql = "update " + messageId + " set status = 'deleted'";

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
