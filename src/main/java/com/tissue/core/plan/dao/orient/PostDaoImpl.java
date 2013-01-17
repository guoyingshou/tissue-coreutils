package com.tissue.core.plan.dao.orient;

import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.mapper.PostMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Post;
import com.tissue.core.plan.dao.PostDao;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;

@Component
public class PostDaoImpl extends OrientDao implements PostDao {

    /**
    public Post create(Post post) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = PostMapper.convert(post);
            saveDoc(doc);

            String ridPost = doc.getIdentity().toString();
            String ridPlan = OrientIdentityUtil.decode(post.getPlan().getId());
            String ridUser = OrientIdentityUtil.decode(post.getUser().getId());

            String sql = "update " + ridPost + " set plan = " + ridPlan;
            executeCommand(db, sql);

            String sqlpost = "create edge EdgePost from " + ridUser + " to " + ridPost + " set label = 'post', createTime = sysdate()";

            String sqlquestion = "create edge EdgeQuestion from " + ridUser + " to " + ridPost + " set label = 'question', createTime = sysdate()";

            if("question".equals(post.getType())) {
                executeCommand(db, sqlquestion);
            }
            else {
                executeCommand(db, sqlpost);
            }

            post.setId(OrientIdentityUtil.encode(ridPost));
            return post;
        }
        finally {
            db.close();
        }
    }
    */

    public Post update(Post post) {
        String ridPost = OrientIdentityUtil.decode(post.getId());
        String sql = "update " + ridPost + " set title = '" + post.getTitle() + "', content = '" + post.getContent() + "'";

        OGraphDatabase db = dataSource.getDB();
        try {
            executeCommand(db, sql);
            return post;
        }
        finally {
            db.close();
        }
    }

    public Post getPost(String id) {
        String sql = "select from " + OrientIdentityUtil.decode(id);
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = querySingle(db, sql);
            return PostMapper.buildPostDetails(doc);
        }
        finally {
            db.close();
        }
    }

    public List<Post> getPagedPostsByTopicId(String topicId, int page, int size) {
        String sql = "select from Post where plan.topic in " + OrientIdentityUtil.decode(topicId) + " order by createTime desc skip " + ((page - 1) * size) + " limit " + size;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            return PostMapper.buildPosts(docs);
        }
        finally {
            db.close();
        }
    }

    public long getPostsCountByTopicId(String topicId) {
        long result = 0;
        String sql = "select count(*) from Post where plan.topic in " + OrientIdentityUtil.decode(topicId);
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = querySingle(db, sql);
            result = doc.field("count", long.class);
            return result;
        }
        finally {
            db.close();
        }
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
        System.out.println("count: " + count);
        return count;
    }

    public List<Post> getPagedPostsByTopicIdAndType(String topicId, String type, int page, int size) {
        String ridTopic = OrientIdentityUtil.decode(topicId);
        String sql = "select from Post where plan.topic in " + ridTopic + " and type = '" + type + "' order by createTime desc skip " + (page - 1) * size + " limit " + size;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            return PostMapper.buildPosts(docs);
        }
        finally {
            db.close();
        }
    }

    public long getPostsCountByPlanId(String planId) {
        long result = 0;

        String ridPlan = OrientIdentityUtil.decode(planId);
        String sql = "select count(*) from Post where plan in " + ridPlan;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = querySingle(db, sql);
            result = doc.field("count", long.class);
            return result;
        }
        finally {
            db.close();
        }
    }

    public List<Post> getPagedPostsByPlanId(String planId, int page, int size) {
        String ridPlan = OrientIdentityUtil.decode(planId);
        String sql = "select from Post where plan in " + ridPlan + " order by createTime desc skip " + (page - 1) * size + " limit " + size;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            return PostMapper.buildPosts(docs);
        }
        finally {
            db.close();
        }
    }

    /**
    public List<Post> getPostsByPlanId(String planId) {
        String ridPlan = OrientIdentityUtil.decode(planId);
        String sql = "select from Post where plan in " + ridPlan;

        List<ODocument> docs = query(sql);
        return PostMapper.buildPosts(docs);
    }
    */

    //-- by user
    
    public long getPostsCountByUserId(String userId) {
        long result = 0;

        String rid = OrientIdentityUtil.decode(userId);
        String sql = "select count(*) from Post where in.out in " + rid;

        OGraphDatabase db = dataSource.getDB();
        try {
        ODocument doc = querySingle(db, sql);
        result = doc.field("count", long.class);
        return result;
        }
        finally {
            db.close();
        }
    }

    public List<Post> getPagedPostsByUserId(String userId, int page, int size) {
        String rid = OrientIdentityUtil.decode(userId);
        String sql = "select from Post where in.out in " + rid + " order by createTime desc skip " + (page - 1) * size + " limit " + size;

        OGraphDatabase db = dataSource.getDB();
        try {
        List<ODocument> docs = query(db, sql);
        return PostMapper.buildPosts(docs);
        }
        finally {
            db.close();
        }
    }

}
