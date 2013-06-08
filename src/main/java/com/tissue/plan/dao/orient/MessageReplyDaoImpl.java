package com.tissue.plan.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.core.dao.orient.ContentDaoImpl;
import com.tissue.plan.dao.MessageReplyDao;
import com.tissue.plan.command.MessageReplyCommand;
import com.tissue.plan.mapper.TopicMapper;
import com.tissue.plan.mapper.PlanMapper;
import com.tissue.plan.mapper.ArticleMapper;
import com.tissue.plan.mapper.MessageMapper;
import com.tissue.plan.mapper.MessageReplyMapper;
import com.tissue.plan.Topic;
import com.tissue.plan.Plan;
import com.tissue.plan.Article;
import com.tissue.plan.Message;
import com.tissue.plan.MessageReply;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;


import java.util.List;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MessageReplyDaoImpl extends ContentDaoImpl implements MessageReplyDao {

    private static Logger logger = LoggerFactory.getLogger(MessageReplyDaoImpl.class);

    public String create(MessageReplyCommand command) {
        String messageId = command.getMessage().getId();
        String accountId = command.getAccount().getId();

        OrientGraph db = dataSource.getDB();
        try {
            ODocument doc = MessageReplyMapper.convertMessageReply(command);
            doc.save();
            String replyId = doc.getIdentity().toString();

            String sql = "create edge Contains from " + messageId + " to " + replyId;
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "create edge Owns from " + accountId + " to " + replyId + " set category = 'messageReply', createTime = sysdate()";
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            return replyId;
        }
        finally {
            db.shutdown();
        }
    }

    /**
     * Get the message reply given it's ID.
     * When trying to delete the reply, the access manager need to check permissions by retrieving all nessary info.
     */
    public MessageReply getMessageReply(String messageReplyId) {
        String sql = "select @this as reply, " +
                     "in_Contains as message, " +
                     "in_Contains.in_Contains as article, " +
                     "in_Contains.in_Contains.in_Contains as plan, " +
                     "in_Contains.in_Contains.in_Contains.in_Contains as topic " +
                     "from " + messageReplyId;
        logger.debug(sql);

        MessageReply messageReply = null;

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            for(ODocument doc : docs) {
                ODocument replyDoc = doc.field("reply");
                messageReply = MessageReplyMapper.buildMessageReply(replyDoc);

                ODocument messageDoc = doc.field("message");
                Message message = MessageMapper.buildMessage(messageDoc);
                messageReply.setMessage(message);

                ODocument articleDoc = doc.field("article");
                Article article = ArticleMapper.buildArticle(articleDoc);
                message.setArticle(article);

                ODocument planDoc = doc.field("plan");
                Plan plan = PlanMapper.buildPlan(planDoc);
                article.setPlan(plan);

                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                plan.setTopic(topic);
            }
        }
        finally {
           db.shutdown();
        }
        return messageReply;
    }
}
