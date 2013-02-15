package com.tissue.core.plan.dao.orient;

import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.util.OrientIdentityUtil;
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
import com.tissue.core.plan.command.PostCommand;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

@Component
public class PostDaoImpl extends OrientDao implements PostDao {

    public String create(PostCommand postCommand) {
        String postId = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = PostMapper.convert(postCommand);
            saveDoc(doc);

            String ridPost = doc.getIdentity().toString();
            String ridUser = OrientIdentityUtil.decode(postCommand.getUser().getId());
            String ridPlan = OrientIdentityUtil.decode(postCommand.getPlan().getId());

            String sql = "update " + ridPost + " set plan = " + ridPlan;
            executeCommand(db, sql);

            sql = "create edge EdgePost from " + ridUser + " to " + ridPost + " set createTime = sysdate(), label = '" + postCommand.getType() + "'";
            executeCommand(db, sql);

            postId = OrientIdentityUtil.encode(ridPost);
        }
        finally {
            db.close();
        }
        return postId;
    }

    public Post update(Post post) {
        String ridPost = OrientIdentityUtil.decode(post.getId());
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = db.load(new ORecordId(ridPost));
            doc.field("title", post.getTitle());
            doc.field("content", post.getContent());
            doc.save();
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return post;
    }

    public Post getPost(String id) {
        Post post = null;

        String ridPost = OrientIdentityUtil.decode(id);
        String sql = "select from " + ridPost;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = querySingle(db, sql);
            post = PostMapper.buildPostDetails(doc);
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return post;
    }

    public List<Post> getPagedPostsByTopicId(String topicId, int page, int size) {
        List<Post> posts = new ArrayList();

        String ridTopic = OrientIdentityUtil.decode(topicId);
        String sql = "select from post where plan.topic in " + ridTopic + " order by createTime desc skip " + ((page - 1) * size) + " limit " + size;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            for(ODocument doc : docs) {
                Post post = PostMapper.buildPost(doc);
                posts.add(post);
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return posts;
    }

    public long getPostsCountByTopicId(String topicId) {
        long result = 0;
        String sql = "select count(*) from Post where plan.topic in " + OrientIdentityUtil.decode(topicId);
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = querySingle(db, sql);
            result = doc.field("count", long.class);
        }
        finally {
            db.close();
        }
        return result;
    }

    public long getPostsCountByTopicIdAndType(String topicId, String type) {
        long count = 0;
        String ridTopic = OrientIdentityUtil.decode(topicId);
        String sql = "select count(*) from Post where plan.topic in " + ridTopic + " and type = '" + type + "'";

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = querySingle(db, sql);
            count = doc.field("count", long.class);
        }
        catch(Exception exc) {
            //todo
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return count;
    }

    public List<Post> getPagedPostsByTopicIdAndType(String topicId, String type, int page, int size) {
        List<Post> posts = new ArrayList();

        String ridTopic = OrientIdentityUtil.decode(topicId);

        String sql = "select from post where type = '" + type + "' and plan.topic in " + ridTopic + " order by createTime desc skip " + ((page - 1) * size) + " limit " + size;


        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            for(ODocument doc : docs) {
                Post post = PostMapper.buildPost(doc);
                posts.add(post);

            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return posts;
    }

    public long getPostsCountByPlanId(String planId) {
        long result = 0;

        String ridPlan = OrientIdentityUtil.decode(planId);
        String sql = "select count(*) from Post where plan in " + ridPlan;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = querySingle(db, sql);
            result = doc.field("count", long.class);
        }
        finally {
            db.close();
        }
        return result;
    }

    public List<Post> getPagedPostsByPlanId(String planId, int page, int size) {
        List<Post> posts = new ArrayList();

        String ridPlan = OrientIdentityUtil.decode(planId);
        String sql = "select from Post where plan in " + ridPlan + " order by createTime desc skip " + (page - 1) * size + " limit " + size;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            for(ODocument doc : docs) {
                Post post = PostMapper.buildPost(doc);
                posts.add(post);
            }
            //posts = PostMapper.buildPosts(docs);
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return posts;
    }
    
    public long getPostsCountByUserId(String userId) {
        long result = 0;

        String rid = OrientIdentityUtil.decode(userId);
        String sql = "select count(*) from Post where in.out in " + rid;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = querySingle(db, sql);
            result = doc.field("count", long.class);
        }
        finally {
            db.close();
        }
        return result;
    }

    public List<Post> getPagedPostsByUserId(String userId, int page, int size) {
        List<Post> posts = new ArrayList();

        String rid = OrientIdentityUtil.decode(userId);
        String sql = "select from Post where in.out in " + rid + " order by createTime desc skip " + (page - 1) * size + " limit " + size;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            for(ODocument doc : docs) {
                Post post = PostMapper.buildPost(doc);
                posts.add(post);
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
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
            List<ODocument> docs = query(db, sql);
            for(ODocument doc : docs) {
                Post post = PostMapper.buildPost(doc);
                posts.add(post);
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return posts;
    }

    public Topic getTopic(String postId) {
        Topic topic = null;

        String ridPost = OrientIdentityUtil.decode(postId);
        String sql = "select plan.topic as topic from " + ridPost;
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = querySingle(db, sql);
            ODocument topicDoc = doc.field("topic");
            topic = TopicMapper.buildTopicDetails(topicDoc);
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return topic;
    }

}
