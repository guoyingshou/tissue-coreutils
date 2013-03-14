package com.tissue.core.plan.dao.orient;

import com.tissue.core.command.ArticleCommand;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.mapper.TopicMapper;
//import com.tissue.core.mapper.UserMapper;
import com.tissue.core.mapper.ArticleMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Article;
//import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Topic;
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
public class ArticleDaoImpl implements ArticleDao {

    private static Logger logger = LoggerFactory.getLogger(ArticleDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public String create(ArticleCommand command) {
        String id = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = ArticleMapper.convertArticle(command);
            db.save(doc);

            id = doc.getIdentity().toString();
            String accountId = command.getAccount().getId();
 
            String sql = "create edge EdgePost from " + accountId + " to " + id + " set createTime = sysdate(), label = '" + command.getType() + "'";
            logger.debug(sql);

            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            if(command.getPlan() != null) {
                String planId = command.getPlan().getId();
                sql = "update " + id + " set plan = " + planId;
                logger.debug(sql);

                cmd = new OCommandSQL(sql);
                db.command(cmd).execute();
            }
        }
        finally {
            db.close();
        }
        return id;
    }

    public Article getArticle(String id) {
        String sql = "select from " + id;
        logger.debug(sql);

        Article article = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                article = ArticleMapper.buildArticleDetails(doc);
            }
        }
        finally {
            db.close();
        }
        return article;
    }

    public Topic getTopic(String articleId) {
        String sql = "select plan.topic as topic from " + articleId;
        logger.debug(sql);

        Topic topic = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                ODocument topicDoc = doc.field("topic");
                topic = TopicMapper.buildTopic(topicDoc);
            }
        }
        finally {
            db.close();
        }
        return topic;
    }

    /**
    public List<Post> getLatestPosts(int limit) {
        List<Post> posts = new ArrayList();

        OGraphDatabase db = dataSource.getDB();
        try {
            String sql = "select from Post where deleted is null and plan.topic.deleted is null and type contains ['concept', 'note', 'tutorial'] order by createTime desc limit " + limit;
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                Post post = PostMapper.buildPostSelf(doc);
                posts.add(post);
            }
        }
        finally {
            db.close();
        }
        return posts;
    }
    */

}
