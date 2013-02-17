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

    public String create(PostMessageCommand message) {
        String id = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = PostMessageMapper.convertPostMessage(message);
            db.save(doc);

            id = doc.getIdentity().toString();
            String postId = message.getPost().getId();
            String userId = message.getUser().getId();

            String sql = "update " + id + " set post = " + postId;
            //executeCommand(db, sql);
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "create edge EdgePost from " + userId + " to " + id + " set label = 'postMessage', createTime = sysdate()";
            //executeCommand(db, sql);
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + postId + " add messages = " + id;
            //executeCommand(db, sql);
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            //message.setId(OrientIdentityUtil.encode(ridMessage));
        }
        finally {
            db.close();
        }
        return id;
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
