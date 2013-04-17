package com.tissue.plan.dao.orient;

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

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PostDaoImpl extends ContentDaoImpl implements PostDao {

    private static Logger logger = LoggerFactory.getLogger(PostDaoImpl.class);

    public String create(PostCommand command) {
        String id = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = PostMapper.convertPost(command);
            db.save(doc);

            id = doc.getIdentity().toString();
            String accountId = command.getAccount().getId();
 
            String sql = "create edge EdgeCreatePost from " + accountId + " to " + id + " set createTime = sysdate(), label = '" + command.getType() + "'";
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

    public void update(PostCommand command) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = db.load(new ORecordId(command.getId()));
            doc.field("title", command.getTitle());
            doc.field("content", command.getContent());
            doc.save();
        }
        finally {
            db.close();
        }
    }

    public List<Post> getLatestPosts(int limit) {
        List<Post> posts = new ArrayList();

        //String sql = "select from Post where deleted is null and plan.topic.deleted is null and type in ['concept', 'note', 'tutorial', 'question'] order by createTime desc limit " + limit;
        String sql = "select in as post from EdgeCreatePost where in.deleted is null and in.plan.topic.deleted is null and in.type in ['concept', 'note', 'tutorial', 'question'] order by createTime desc limit " + limit;
        logger.debug(sql);

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                ODocument postDoc = doc.field("post");
                Post post = PostMapper.buildPost(postDoc);
                posts.add(post);
            }
        }
        finally {
            db.close();
        }
        return posts;
    }

    public long getPostsCountByUser(String userId) {
        String sql = "select count(*) from Post where deleted is null and type in ['concept', 'note', 'tutorial', 'question'] and in.out.user in " + userId;
        logger.debug(sql);

        long count = 0;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                count = doc.field("count", long.class);
            }
        }
        finally {
            db.close();
        }
        return count;
    }

    public List<Post> getPagedPostsByUser(String userId, int page, int size) {
        String sql = "select from Post where deleted is null and type in ['concept', 'note', 'tutorial', 'question'] and in.out.user in " + userId + " order by createTime desc skip " + (page - 1) * size + " limit " + size;
        logger.debug(sql);

        List<Post> posts = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                Post post = PostMapper.buildPost(doc);
                posts.add(post);
            }
        }
        finally {
            db.close();
        }
        return posts;
    }

    public long getPostsCountByPlan(String planId) {
        long count = 0;

        String sql = "select count(*) from Post where deleted is null and plan in " + planId;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                count = doc.field("count", long.class);
            }
        }
        finally {
            db.close();
        }
        return count;
    }

    public List<Post> getPagedPostsByPlan(String planId, int page, int size) {
        List<Post> posts = new ArrayList();
        //String sql = "select from Post where deleted is null and plan in " + planId + " order by createTime desc skip " + (page - 1) * size + " limit " + size;
        String sql = "select in as post from EdgeCreatePost where in.deleted is null and in.plan in " + planId + " order by createTime desc skip " + (page - 1) * size + " limit " + size;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                ODocument postDoc = doc.field("post");
                Post post = PostMapper.buildPost(postDoc);
                posts.add(post);
            }
        }
        finally {
            db.close();
        }
        return posts;
    }
 
    public long getPostsCountByTopic(String topicId) {
        long count = 0;
        String sql = "select count(*) from Post where deleted is null and plan.topic in " + topicId;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                count = doc.field("count", long.class);
            }
        }
        finally {
            db.close();
        }
        return count;
    }

    public List<Post> getPagedPostsByTopic(String topicId, int page, int size) {
        List<Post> posts = new ArrayList();
        //String sql = "select from post where deleted is null and plan.topic in " + topicId + " order by createTime desc skip " + ((page - 1) * size) + " limit " + size;
        String sql = "select in as post from EdgeCreatePost where in.deleted is null and in.plan.topic in " + topicId + " order by createTime desc skip " + ((page - 1) * size) + " limit " + size;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                ODocument postDoc = doc.field("post");
                Post post = PostMapper.buildPost(postDoc);
                posts.add(post);
            }
        }
        finally {
            db.close();
        }
        return posts;
    }

    public long getPostsCountByType(String topicId, String type) {
        long count = 0;
        String sql = "select count(*) from Post where deleted is null and plan.topic in " + topicId + " and type = '" + type + "'";

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                count = doc.field("count", long.class);
            }
        }
        finally {
            db.close();
        }
        return count;
    }

    public List<Post> getPagedPostsByType(String topicId, String type, int page, int size) {
        List<Post> posts = new ArrayList<Post>();

        //String sql = "select from post where deleted is null and type = '" + type + "' and plan.topic in " + topicId + " order by createTime desc skip " + ((page - 1) * size) + " limit " + size;
        String sql = "select in as post from EdgeCreatePost where in.deleted is null and in.type = '" + type + "' and in.plan.topic in " + topicId + " order by createTime desc skip " + ((page - 1) * size) + " limit " + size;


        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                ODocument postDoc = doc.field("post");
                Post post = PostMapper.buildPost(doc);
                posts.add(post);
            }
        }
        finally {
            db.close();
        }
        return posts;
    }

}
