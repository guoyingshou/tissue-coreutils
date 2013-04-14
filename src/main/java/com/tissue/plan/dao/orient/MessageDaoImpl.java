package com.tissue.plan.dao.orient;

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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class MessageDaoImpl extends ContentDaoImpl implements MessageDao {

    private static Logger logger = LoggerFactory.getLogger(MessageDaoImpl.class);

    public String create(MessageCommand command) {
        String id = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = MessageMapper.convertMessage(command);
            db.save(doc);

            id = doc.getIdentity().toString();
            String postId = command.getArticle().getId();
            String userId = command.getAccount().getId();

            String sql = "update " + id + " set article = " + postId;
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "create edge EdgeCreate from " + userId + " to " + id + " set label = 'message', createTime = sysdate()";
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

    public Message getMessage(String messageId) {
        String sql = "select from " + messageId;
        logger.debug(sql);

        Message message = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                message = MessageMapper.buildMessage(doc);

                ODocument articleDoc = doc.field("article");
                Article article = ArticleMapper.buildArticle(articleDoc);
                message.setArticle(article);

                ODocument planDoc = articleDoc.field("plan");
                Plan plan = PlanMapper.buildPlan(planDoc);
                article.setPlan(plan);

                ODocument topicDoc = planDoc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                plan.setTopic(topic);

                List<ODocument> plansDoc = topicDoc.field("plans");
                if(plansDoc != null) {
                    for(ODocument topicPlanDoc : plansDoc) {
                        Plan topicPlan = PlanMapper.buildPlan(topicPlanDoc);
                        topic.addPlan(topicPlan);
                    }
                }
            }
        }
        finally {
           db.close();
        }
        return message;
    }

}
