package com.tissue.core.plan.dao.orient;

import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.mapper.PostMessageMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Post;
import com.tissue.core.plan.PostMessage;
import com.tissue.core.plan.dao.PostMessageDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;

@Component
public class PostMessageDaoImpl extends OrientDao implements PostMessageDao {

    public PostMessage create(PostMessage message) {

        OGraphDatabase db = dataSource.getDB();
        try {
        ODocument doc = PostMessageMapper.convertPostMessage(message);
        saveDoc(doc);

        String ridMessage = doc.getIdentity().toString();

        String ridPost = OrientIdentityUtil.decode(message.getPost().getId());
        String ridUser = OrientIdentityUtil.decode(message.getUser().getId());

        String sql = "update " + ridMessage + " set post = " + ridPost;

        String sql2 = "create edge EdgePostMessage from " + ridUser + " to " + ridMessage + " set label = 'postMessage', createTime = sysdate()";

        String sql3 = "update " + ridPost + " add messages = " + ridMessage;

        executeCommand(db, sql);
        executeCommand(db, sql2);
        executeCommand(db, sql3);
 
        message.setId(OrientIdentityUtil.encode(ridMessage));
        return message;
        }
        finally {
            db.close();
        }
    }

    public void update(PostMessage message) {

        String ridMessage = OrientIdentityUtil.decode(message.getId());
        String sql = "update " + ridMessage + " set content = '" + message.getContent() + "'";

        OGraphDatabase db = dataSource.getDB();
        try {
        executeCommand(db, sql);
        }
        finally {
            db.close();
        }
    }

    public void delete(String messageId) {
        String ridMessage = OrientIdentityUtil.decode(messageId);
        String sql = "update " + ridMessage + " set status = 'deleted'";

        OGraphDatabase db = dataSource.getDB();
        try {
        executeCommand(db, sql);
        }
        finally {
            db.close();
        }
    }
}
