package com.tissue.plan.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.User;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.core.mapper.UserMapper;
import com.tissue.core.dao.orient.ContentDaoImpl;
import com.tissue.plan.dao.PostDao;
import com.tissue.plan.command.PostCommand;
import com.tissue.plan.mapper.PostMapper;
import com.tissue.plan.Post;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.blueprints.Vertex;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PostDaoImpl extends ContentDaoImpl implements PostDao {

    private static Logger logger = LoggerFactory.getLogger(PostDaoImpl.class);

    public String create(PostCommand command) {

        String accountId = command.getAccount().getId();
        String planId = command.getPlan().getId();

        OrientGraph db = dataSource.getDB();
        try {
            ODocument doc = PostMapper.convertPost(command);
            doc.save();
            String postId = doc.getIdentity().toString();
            
            String sql = "create edge Owns from " + accountId + " to " + postId + " set createTime = sysdate(), category = '" + command.getType() + "'";
            logger.debug(sql);
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            sql = "create edge Contains from " + planId + " to " + postId;
            logger.debug(sql);
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            return postId;
        }
        finally {
            db.shutdown();
        }
    }

    public void update(PostCommand command) {
        OrientGraph db = dataSource.getDB();
        try {
            OrientVertex v = db.getVertex(command.getId());
            v.setProperty("title", command.getTitle());
            v.setProperty("content", command.getContent());
        }
        finally {
            db.shutdown();
        }
    }

    public List<Post> getLatestPosts(int limit) {
        String sql = "select @this as post, in_Owns.createTime as createTime, deleted " +
                     "from Post " + 
                     "where deleted is null " +
                     "and in_Contains.in_Contains.deleted is null " +
                     "order by createTime desc " +
                     "limit " + limit;
        logger.debug(sql);

        List<Post> posts = new ArrayList();
        OrientGraph db = dataSource.getDB();
        try {
            posts = buildPosts(db, sql);
        }
        finally {
            db.shutdown();
        }
        return posts;
    }
 
    public long getPostsCountByTopic(String topicId) {
        String sql = "select count(*) from Post where deleted is null and in_Contains.in_Contains in " + topicId;
        logger.debug(sql);

        long count = 0;

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql)).execute();
            for(ODocument doc : docs) {
                count = doc.field("count", long.class);
            }
        }
        finally {
            db.shutdown();
        }
        return count;
    }

    public List<Post> getPagedPostsByTopic(String topicId, int page, int size) {
        String sql = "select @this as post, in_Owns.createTime as createTime, deleted " +
                     "from Post " +
                     "where deleted is null " +
                     "and in_Contains.in_Contains in " + topicId + 
                     " order by createTime desc " +
                     "skip " + ((page - 1) * size) + 
                     " limit " + size;
        logger.debug(sql);

        List<Post> posts = new ArrayList();

        OrientGraph db = dataSource.getDB();
        try {
            posts = buildPosts(db, sql);
        }
        finally {
            db.shutdown();
        }
        return posts;
    }

    public long getPostsCountByType(String topicId, String type) {
        String sql = "select count(*) from Post where deleted is null and in_Contains.in_Contains in " + topicId + " and type = '" + type + "'";
        logger.debug(sql);

        long count = 0;
        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql)).execute();
            for(ODocument doc : docs) {
                count = doc.field("count", long.class);
            }
        }
        finally {
            db.shutdown();
        }
        return count;
    }

    public List<Post> getPagedPostsByType(String topicId, String type, int page, int size) {

        String sql = "select @this as post, in_Owns.createTime as createTime, deleted, type " +
                     "from Post " +
                     "where deleted is null " +
                     "and type = '" + type + 
                     "' and in_Contains.in_Contains in " + topicId + 
                     " order by createTime desc " +
                     " skip " + ((page - 1) * size) + 
                     " limit " + size;
        logger.debug(sql);

        List<Post> posts = new ArrayList<Post>();

        OrientGraph db = dataSource.getDB();
        try {
            posts = buildPosts(db, sql);
        }
        finally {
            db.shutdown();
        }
        return posts;
    }

    public long getPostsCountByPlan(String planId) {
        long count = 0;

        String sql = "select count(*) from Post where deleted is null and in_Contains in " + planId;
        logger.debug(sql);

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql)).execute();
            for(ODocument doc : docs) {
                count = doc.field("count", long.class);
            }
        }
        finally {
            db.shutdown();
        }
        return count;
    }

    public List<Post> getPagedPostsByPlan(String planId, int page, int size) {
        String sql = "select @this as post, in_Owns.createTime as createTime, deleted " +
                     "from Post " +
                     "where deleted is null " +
                     "and in_Contains in " + planId + 
                     " order by createTime desc " +
                     " skip " + (page - 1) * size + 
                     " limit " + size;
        List<Post> posts = new ArrayList();
        OrientGraph db = dataSource.getDB();
        try {
            posts = buildPosts(db, sql);
        }
        finally {
            db.shutdown();
        }
        return posts;
    }

    public long getPostsCountByUser(String userId) {
        String sql = "select count(*) from Post " +
                     "where deleted is null " +
                     //"and type in ['concept', 'note', 'tutorial', 'question'] " +
                     "and in_Owns.out_Belongs in " + userId;
        logger.debug(sql);

        long count = 0;

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql)).execute();
            for(ODocument doc : docs) {
                count = doc.field("count", long.class);
            }
        }
        finally {
            db.shutdown();
        }
        return count;
    }

    public List<Post> getPagedPostsByUser(String userId, int page, int size) {
        String sql = "select @this as post, in_Owns.createTime as createTime, deleted " +
                     " from Post " +
                     " where deleted is null " +
                     " and in_Owns.out.out_Belongs in " + userId + 
                     " order by createTime desc " +
                     " skip " + (page - 1) * size + 
                     " limit " + size;
        logger.debug(sql);

        List<Post> posts = new ArrayList();

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            for(ODocument doc : docs) {
                ODocument postDoc = doc.field("post");
                Post post = PostMapper.buildPost(postDoc);

                posts.add(post);
            }
        }
        finally {
            db.shutdown();
        }
        return posts;
    }

    private List<Post> buildPosts(OrientGraph db, String sql) {
        List<Post> posts = new ArrayList<Post>();

        Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
        for(ODocument doc : docs) {
            ODocument postDoc = doc.field("post");
            Post post = PostMapper.buildPost(postDoc);

            posts.add(post);
        }
        return posts;
    }

}
