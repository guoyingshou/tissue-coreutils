package com.tissue.core.plan.dao.orient;

import com.tissue.core.util.OrientDataSource;
import com.tissue.core.command.MessageCommand;
import com.tissue.core.mapper.TopicMapper;
import com.tissue.core.mapper.PlanMapper;
import com.tissue.core.mapper.ArticleMapper;
import com.tissue.core.mapper.MessageMapper;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Article;
import com.tissue.core.plan.Message;
import com.tissue.core.plan.dao.MessageDao;

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
public class MessageDaoImpl implements MessageDao {

    private static Logger logger = LoggerFactory.getLogger(MessageDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public String create(MessageCommand command) {
        String id = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = MessageMapper.convertMessage(command);
            db.save(doc);

            id = doc.getIdentity().toString();
            String postId = command.getArticle().getId();
            String userId = command.getAccount().getId();

            String sql = "update " + id + " set post = " + postId;
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "create edge EdgePost from " + userId + " to " + id + " set label = 'postMessage', createTime = sysdate()";
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
            }
        }
        finally {
           db.close();
        }
        return message;
    }


    public void update(MessageCommand command) {

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
