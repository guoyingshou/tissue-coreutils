package com.tissue.core.plan.dao.orient;

import com.tissue.core.dao.orient.ContentDaoImpl;
import com.tissue.core.plan.dao.MessageReplyDao;
import com.tissue.core.plan.command.MessageReplyCommand;
import com.tissue.core.plan.mapper.TopicMapper;
import com.tissue.core.plan.mapper.PlanMapper;
import com.tissue.core.plan.mapper.ArticleMapper;
import com.tissue.core.plan.mapper.MessageMapper;
import com.tissue.core.plan.mapper.MessageReplyMapper;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Article;
import com.tissue.core.plan.Message;
import com.tissue.core.plan.MessageReply;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MessageReplyDaoImpl extends ContentDaoImpl implements MessageReplyDao {

    private static Logger logger = LoggerFactory.getLogger(MessageReplyDaoImpl.class);

    public String create(MessageReplyCommand command) {
        String id = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = MessageReplyMapper.convertMessageReply(command);
            db.save(doc);

            id = doc.getIdentity().toString();
            String msgId = command.getMessage().getId();
            String userId = command.getAccount().getId();

            String sql = "update " + id + " set message = " + msgId;
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "create edge EdgePost from " + userId + " to " + id + " set label = 'messageReply', createTime = sysdate()";
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        
            sql = "update " + msgId + " add replies = " + id;
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
        return id;
    }

    public MessageReply getMessageReply(String messageReplyId) {
        String sql = "select from " + messageReplyId;
        logger.debug(sql);

        MessageReply messageReply = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                messageReply = MessageReplyMapper.buildMessageReply(doc);

                ODocument messageDoc = doc.field("message");
                Message message = MessageMapper.buildMessage(messageDoc);
                messageReply.setMessage(message);

                ODocument articleDoc = messageDoc.field("article");
                Article article = ArticleMapper.buildArticle(articleDoc);
                message.setArticle(article);

                ODocument planDoc = articleDoc.field("plan");
                Plan plan = PlanMapper.buildPlan(planDoc);
                article.setPlan(plan);

                ODocument topicDoc = planDoc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                plan.setTopic(topic);
            }
        }
        finally {
           db.close();
        }
        return messageReply;
    }
}
