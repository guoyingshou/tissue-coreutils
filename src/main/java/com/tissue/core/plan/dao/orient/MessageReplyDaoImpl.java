package com.tissue.core.plan.dao.orient;

import com.tissue.core.command.MessageReplyCommand;
import com.tissue.core.util.OrientDataSource;

import com.tissue.core.mapper.MessageReplyMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Message;
import com.tissue.core.plan.MessageReply;
import com.tissue.core.plan.dao.MessageReplyDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;


@Component
public class MessageReplyDaoImpl implements MessageReplyDao {
    @Autowired
    protected OrientDataSource dataSource;

    public String create(MessageReplyCommand command) {
        String id = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = MessageReplyMapper.convertMessageReply(command);
            db.save(doc);

            id = doc.getIdentity().toString();
            String msgId = command.getMessage().getId();
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
        }
        finally {
            db.close();
        }
        return id;
    }

    public void update(MessageReplyCommand command) {
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
