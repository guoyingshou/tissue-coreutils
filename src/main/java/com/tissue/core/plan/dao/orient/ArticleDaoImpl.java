package com.tissue.core.plan.dao.orient;

import com.tissue.core.mapper.TopicMapper;
import com.tissue.core.mapper.PlanMapper;
import com.tissue.core.mapper.ArticleMapper;
import com.tissue.core.mapper.MessageMapper;
import com.tissue.core.mapper.MessageReplyMapper;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Article;
import com.tissue.core.plan.Message;
import com.tissue.core.plan.MessageReply;
import com.tissue.core.plan.dao.ArticleDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ArticleDaoImpl extends PostDaoImpl implements ArticleDao {

    private static Logger logger = LoggerFactory.getLogger(ArticleDaoImpl.class);

    public Article getArticle(String id) {
        String sql = "select from " + id;
        logger.debug(sql);

        Article article = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:4"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                article = ArticleMapper.buildArticle(doc);

                ODocument planDoc = doc.field("plan");
                Plan plan = PlanMapper.buildPlan(planDoc);
                article.setPlan(plan);

                ODocument topicDoc = planDoc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                plan.setTopic(topic);

                List<ODocument> topicPlansDoc = topicDoc.field("plans");
                if(topicPlansDoc != null) {
                    for(ODocument topicPlanDoc : topicPlansDoc) {
                        Plan topicPlan = PlanMapper.buildPlan(topicPlanDoc);
                        topic.addPlan(topicPlan);
                    }
                }

                List<ODocument> messagesDoc = doc.field("messages");
                if(messagesDoc != null) {
                    for(ODocument messageDoc : messagesDoc) {
                        Message message = MessageMapper.buildMessage(messageDoc);
                        article.addMessage(message);

                        List<ODocument> repliesDoc = messageDoc.field("replies");
                        if(repliesDoc != null) {
                            for(ODocument replyDoc : repliesDoc) {
                                MessageReply reply = MessageReplyMapper.buildMessageReply(replyDoc);
                                message.addReply(reply);
                            }
                        }
                    }
                }
            }
        }
        finally {
            db.close();
        }
        return article;
    }

}
