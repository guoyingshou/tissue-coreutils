package com.tissue.plan.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.core.dao.orient.ContentDaoImpl;
import com.tissue.plan.dao.MessageDao;
import com.tissue.plan.command.MessageCommand;
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

import org.springframework.stereotype.Repository;
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

@Repository
public class MessageDaoImpl extends ContentDaoImpl implements MessageDao {

    private static Logger logger = LoggerFactory.getLogger(MessageDaoImpl.class);

    public String create(MessageCommand command) {
        String postId = command.getArticle().getId();
        String accountId = command.getAccount().getId();

        OrientGraph db = dataSource.getDB();
        try {
            ODocument doc = MessageMapper.convertMessage(command);
            doc.save();
            String messageId = doc.getIdentity().toString();

            String sql = "create edge MessagesArticle from " + messageId + " to " + postId;
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "create edge PostAccount from " + messageId + " to " + accountId + " set category = 'message', createTime = sysdate()";
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            return messageId;
        }
        finally {
            db.shutdown();
        }
    }

    public Message getMessage(String messageId) {
        String sql = "select @this as message, " + 
                     "out_PostAccount as account, " + 
                     "out_MessagesArticle as article, " + 
                     "out_MessagesArticle.out_PostsPlan as plan, " + 
                     "out_MessagesArticle.out_PostsPlan.out_PlansTopic as topic, " + 
                     "out('MessagesArticle').out('PostsPlan').out('PlansTopic').in('PlansTopic') as topicPlans " +
                     "from " + messageId;
        logger.debug(sql);

        Message message = null;
        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:2")).execute();
            for(ODocument doc : docs) {
                ODocument messageDoc = doc.field("message");
                message = MessageMapper.buildMessage(messageDoc);
                
                ODocument articleDoc = doc.field("article");
                Article article = ArticleMapper.buildArticle(articleDoc);
                message.setArticle(article);

                ODocument planDoc = doc.field("plan");
                Plan plan = PlanMapper.buildPlan(planDoc);
                article.setPlan(plan);

                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                plan.setTopic(topic);

                List<ODocument> topicPlanDocs = doc.field("topicPlans");
                for(ODocument topicPlanDoc : topicPlanDocs) {
                    Plan topicPlan = PlanMapper.buildPlan(topicPlanDoc);
                    topic.addPlan(topicPlan);
                }
            }
        }
        finally {
           db.shutdown();
        }
        return message;
    }

}
