package com.tissue.plan.dao.orient;

import com.tissue.core.dao.orient.ContentDaoImpl;
import com.tissue.plan.dao.ArticleDao;
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
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
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