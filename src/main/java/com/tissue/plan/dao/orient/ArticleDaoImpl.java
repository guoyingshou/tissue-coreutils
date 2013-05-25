package com.tissue.plan.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.User;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.core.mapper.UserMapper;
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

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;


import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class ArticleDaoImpl extends PostDaoImpl implements ArticleDao {

    private static Logger logger = LoggerFactory.getLogger(ArticleDaoImpl.class);

    public Article getArticle(String id) {
        String sql = "select @this as article, " + 
                     "out_PostsPlan as plan, " + 
                     "out_PostsPlan.out_PlansTopic as topic, " + 
                     "in('MessagesArticle') as messages " + 
                     "from " + id;
 
        logger.debug(sql);

        Article article = null;

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:2")).execute();
            for(ODocument doc : docs) {
                ODocument articleDoc = doc.field("article");
                article = ArticleMapper.buildArticle(articleDoc);

                ODocument planDoc = doc.field("plan");
                Plan plan = PlanMapper.buildPlan(planDoc);
                article.setPlan(plan);

                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                plan.setTopic(topic);

                List<ODocument> messageDocs = doc.field("messages");
                for(ODocument messageDoc : messageDocs) {
                    Object deleted = messageDoc.field("deleted");
                    if(deleted == null) {
                        Message message = MessageMapper.buildMessage(messageDoc);
                        article.addMessage(message);

                        Set<ODocument> replyDocs = new HashSet<ODocument>();
                        Object obj = messageDoc.field("in_RepliesMessage");
                        if(obj != null) {
                            if(obj instanceof ODocument) {
                                replyDocs.add((ODocument)obj);
                            }
                            else {
                                replyDocs = (Set)obj;
                            }

                            for(ODocument replyDoc : replyDocs) {
                                deleted = replyDoc.field("deleted");
                                if(deleted == null) {
                                    MessageReply reply = MessageReplyMapper.buildMessageReply(replyDoc);
                                    message.addReply(reply);
                                }
                            }
                        }
                    }
                }
            }
        }
        finally {
            db.shutdown();
        }
        return article;
    }

}
