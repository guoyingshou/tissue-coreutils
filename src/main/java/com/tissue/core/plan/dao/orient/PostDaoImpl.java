package com.tissue.core.plan.dao.orient;

import com.tissue.core.command.PostCommand;
import com.tissue.core.util.OrientDataSource;

import com.tissue.core.mapper.PostMapper;
import com.tissue.core.mapper.TopicMapper;
import com.tissue.core.mapper.PlanMapper;
import com.tissue.core.mapper.UserMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Post;
import com.tissue.core.plan.PostWrapper;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.dao.PostDao;

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

@Component
public class PostDaoImpl implements PostDao {
    @Autowired
    protected OrientDataSource dataSource;

    public String create(PostCommand postCommand) {

        String id = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = PostMapper.convert(postCommand);
            db.save(doc);
            id = doc.getIdentity().toString();
            String userId = postCommand.getUser().getId();
            String planId = postCommand.getPlan().getId();

            String sql = "update " + id + " set plan = " + planId;
            //executeCommand(db, sql);
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "create edge EdgePost from " + userId + " to " + id + " set createTime = sysdate(), label = '" + postCommand.getType() + "'";
            //executeCommand(db, sql);
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            //postId = OrientIdentityUtil.encode(ridPost);
        }
        finally {
            db.close();
        }
        return id;
    }

    public void update(PostCommand post) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = db.load(new ORecordId(post.getId()));
            doc.field("title", post.getTitle());
            doc.field("content", post.getContent());
            doc.save();
        }
        finally {
            db.close();
        }
    }

    public Post getPost(String id) {
        Post post = null;

        //String ridPost = OrientIdentityUtil.decode(id);
        String sql = "select from " + id;

        OGraphDatabase db = dataSource.getDB();
        try {
            //ODocument doc = querySingle(db, sql);
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                post = PostMapper.buildPostDetails(doc);
            }
        }
        finally {
            db.close();
        }
        return post;
    }

    public List<Post> getPagedPostsByTopicId(String topicId, int page, int size) {
        List<Post> posts = new ArrayList();

        //String ridTopic = OrientIdentityUtil.decode(topicId);
        String sql = "select from post where plan.topic in " + topicId + " order by createTime desc skip " + ((page - 1) * size) + " limit " + size;

        OGraphDatabase db = dataSource.getDB();
        try {
            //List<ODocument> docs = query(db, sql);
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

    public long getPostsCountByTopicId(String topicId) {
        long count = 0;
        String sql = "select count(*) from Post where plan.topic in " + topicId;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                count = doc.field("count", long.class);
            }
            // ODocument doc = querySingle(db, sql);
        }
        finally {
            db.close();
        }
        return count;
    }

    public long getPostsCountByTopicIdAndType(String topicId, String type) {
        long count = 0;
        //String ridTopic = OrientIdentityUtil.decode(topicId);
        String sql = "select count(*) from Post where plan.topic in " + topicId + " and type = '" + type + "'";

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                count = doc.field("count", long.class);
            }
             //ODocument doc = querySingle(db, sql);
        }
        finally {
            db.close();
        }
        return count;
    }

    public List<Post> getPagedPostsByTopicIdAndType(String topicId, String type, int page, int size) {
        List<Post> posts = new ArrayList();

        //String ridTopic = OrientIdentityUtil.decode(topicId);

        String sql = "select from post where type = '" + type + "' and plan.topic in " + topicId + " order by createTime desc skip " + ((page - 1) * size) + " limit " + size;


        OGraphDatabase db = dataSource.getDB();
        try {
            //List<ODocument> docs = query(db, sql);
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

    public long getPostsCountByPlanId(String planId) {
        long count = 0;

        //String ridPlan = OrientIdentityUtil.decode(planId);
        String sql = "select count(*) from Post where plan in " + planId;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                count = doc.field("count", long.class);
            }
            // ODocument doc = querySingle(db, sql);
        }
        finally {
            db.close();
        }
        return count;
    }

    public List<Post> getPagedPostsByPlanId(String planId, int page, int size) {
        List<Post> posts = new ArrayList();

        //String ridPlan = OrientIdentityUtil.decode(planId);
        String sql = "select from Post where plan in " + planId + " order by createTime desc skip " + (page - 1) * size + " limit " + size;

        OGraphDatabase db = dataSource.getDB();
        try {
            //List<ODocument> docs = query(db, sql);
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                Post post = PostMapper.buildPost(doc);
                posts.add(post);
            }
            //posts = PostMapper.buildPosts(docs);
        }
        finally {
            db.close();
        }
        return posts;
    }
    
    public long getPostsCountByUserId(String userId) {
        long count = 0;

        //String rid = OrientIdentityUtil.decode(userId);
        String sql = "select count(*) from Post where in.out in " + userId;

        OGraphDatabase db = dataSource.getDB();
        try {
            //ODocument doc = querySingle(db, sql);
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

    public List<Post> getPagedPostsByUserId(String userId, int page, int size) {
        List<Post> posts = new ArrayList();

//        String rid = OrientIdentityUtil.decode(userId);
        String sql = "select from Post where in.out in " + userId + " order by createTime desc skip " + (page - 1) * size + " limit " + size;

        OGraphDatabase db = dataSource.getDB();
        try {
            //List<ODocument> docs = query(db, sql);
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

    public List<Post> getLatestPosts(int limit) {
        List<Post> posts = new ArrayList();

        OGraphDatabase db = dataSource.getDB();
        try {
            String sql = "select from Post order by createTime desc limit " + limit;
            //List<ODocument> docs = query(db, sql);
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

    public Topic getTopic(String postId) {
        Topic topic = null;

//        String ridPost = OrientIdentityUtil.decode(postId);
        String sql = "select plan.topic as topic from " + postId;
        OGraphDatabase db = dataSource.getDB();
        try {
            //ODocument doc = querySingle(db, sql);
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                ODocument topicDoc = doc.field("topic");
                topic = TopicMapper.buildTopicDetails(topicDoc);
             }
        }
        finally {
            db.close();
        }
        return topic;
    }

}
