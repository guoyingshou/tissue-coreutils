package com.tissue.core.plan.dao.orient;

import com.tissue.core.util.OrientDataSource;
import com.tissue.core.command.MessageCommand;
import com.tissue.core.mapper.MessageMapper;
import com.tissue.core.plan.Message;
import com.tissue.core.plan.dao.MessageDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

@Component
public class MessageDaoImpl implements MessageDao {
    @Autowired
    protected OrientDataSource dataSource;

    public String create(MessageCommand command) {
        String id = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = MessageMapper.convertMessage(command);
            db.save(doc);

            id = doc.getIdentity().toString();
            String postId = command.getArticle().getId();
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
        }
        finally {
            db.close();
        }
        return id;
    }

    public void update(MessageCommand command) {

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
}
