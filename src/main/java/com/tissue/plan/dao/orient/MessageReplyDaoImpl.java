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

import java.util.List;
import java.util.Date;

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
 
            sql = "create edge EdgeCreatePost from " + userId + " to " + id + " set category = 'messageReply', createTime = sysdate()";
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
        String sql = "select @this as reply, in_.out as account, createTime from " + messageReplyId;
        logger.debug(sql);

        MessageReply messageReply = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                ODocument replyDoc = doc.field("reply");
                messageReply = MessageReplyMapper.buildMessageReply(replyDoc);

                ODocument accountDoc = doc.field("account");
                Account account = AccountMapper.buildAccount(accountDoc);
                messageReply.setAccount(account);

                Date ctime = doc.field("createTime", Date.class);
                messageReply.setCreateTime(ctime);

                ODocument messageDoc = replyDoc.field("message");
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
                TopicMapper.postProcessTopic(topic, topicDoc);
                plan.setTopic(topic);
            }
        }
        finally {
           db.close();
        }
        return messageReply;
    }
}
