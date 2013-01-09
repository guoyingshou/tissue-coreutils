package com.tissue.core.plan.dao.orient;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.mapper.PostMapper;
import com.tissue.core.profile.User;
import com.tissue.core.plan.Post;
import com.tissue.core.plan.dao.PostDao;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.nio.charset.Charset;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.tx.OTransaction;

@Component
public class PostDaoImpl implements PostDao {

    @Autowired
    private OrientDataSource dataSource;

    public Post create(Post post) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = PostMapper.convert(post);
            doc.save();

            String ridPost = doc.getIdentity().toString();
            String ridUser = OrientIdentityUtil.decode(post.getUser().getId());

            String sqlpost = "create edge EdgePost from " + 
                          ridUser + 
                          " to " + 
                          ridPost + 
                          " set label = 'post', createTime = sysdate()";

            String sqlquestion = "create edge EdgeQuestion from " + 
                          ridUser + 
                          " to " + 
                          ridPost + 
                          " set label = 'question', createTime = sysdate()";

            OCommandSQL cmd = null;
            if("question".equals(post.getType())) {
                cmd = new OCommandSQL(sqlquestion);
            }
            else {
                cmd = new OCommandSQL(sqlpost);
            }
            db.command(cmd).execute();

            String postId = OrientIdentityUtil.encode(ridPost);
            post.setId(postId);
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return post;
    }

    public Post update(Post post) {
        String ridPost = OrientIdentityUtil.decode(post.getId());
        String sql = "update " + ridPost + " set title = '" + post.getTitle() + "', content = '" + post.getContent() + "'";

        OGraphDatabase db = dataSource.getDB();
        try {

            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
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

        String sql = "select from " + OrientIdentityUtil.decode(id);
        OGraphDatabase db = dataSource.getDB();
        try {
            OSQLSynchQuery<ODocument> q = new OSQLSynchQuery(sql);
            List<ODocument> result = db.query(q.setFetchPlan("*:-1"));
            if(result.size() > 0) {
                ODocument doc = result.get(0);
                post = PostMapper.buildPost(doc);
            }
        }
        catch(Exception exc) {
            //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return post;
    }

    public List<Post> getPostsByTopicId(String topicId) {
        List<Post> posts = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            posts = getPostsByTopicId(db, topicId);
        }
        finally {
            db.close();
        }

        return posts;
    }

    private List<Post> getPostsByTopicId(OGraphDatabase db, String topicId) {
        String postQstr = "select from Post where plan.topic in " + OrientIdentityUtil.decode(topicId);
        OSQLSynchQuery q = new OSQLSynchQuery(postQstr);

        List<ODocument> postsDoc = db.query(q);
        List<Post> posts = PostMapper.buildPosts(postsDoc);
        return posts;
    }

    public List<Post> getPagedPostsByTopicId(String topicId, int page, int size) {
        List<Post> posts = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            posts = getPagedPostsByTopicId(db, topicId, page, size);
        }
        finally {
            db.close();
        }

        return posts;
    }

    private List<Post> getPagedPostsByTopicId(OGraphDatabase db, String topicId, int page, int size) {
        String sql = "select from Post where plan.topic in " + 
                      OrientIdentityUtil.decode(topicId) + 
                      " order by createTime desc skip " + 
                      ((page - 1) * size) + 
                      " limit " + size;
        OSQLSynchQuery q = new OSQLSynchQuery(sql);

        List<ODocument> postsDoc = db.query(q);
        List<Post> posts = PostMapper.buildPosts(postsDoc);
        return posts;
    }


    public long getPostsCountByTopicId(String topicId) {
        long result = 0;

        String sql = "select count(*) from Post where plan.topic in " + OrientIdentityUtil.decode(topicId);
        OGraphDatabase db = dataSource.getDB();
        try {
            OSQLSynchQuery q = new OSQLSynchQuery(sql);
            List<ODocument> postsCountDoc = db.query(q);
            if(postsCountDoc.size() > 0) {
                 ODocument doc = postsCountDoc.get(0);
                 result = doc.field("count", long.class);
            }
        }
        finally {
            db.close();
        }
        return result;
    }


    public long getPostsCountByTopicIdAndType(String topicId, String type) {
        long result = 0;

        String sql = "select count(*) from Post where plan.topic in ? and type = ?";
        OGraphDatabase db = dataSource.getDB();
        try {
            OSQLSynchQuery q = new OSQLSynchQuery(sql);
            List<ODocument> postsCountDoc = db.command(q).execute(OrientIdentityUtil.decode(topicId), type);
            if(postsCountDoc.size() > 0) {
                 ODocument doc = postsCountDoc.get(0);
                 result = doc.field("count", long.class);
            }
        }
        finally {
            db.close();
        }
        return result;
    }

    public List<Post> getPostsByTopicIdAndType(String topicId, String type) {
        List<Post> posts = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            posts = getPostsByTopicIdAndType(db, topicId, type);
        }
        finally {
            db.close();
        }

        return posts;
    }

    public List<Post> getPostsByTopicIdAndType(OGraphDatabase db, String topicId, String type) {
        String postQstr = "select from Post where plan.topic in ? and type = ?";
        OSQLSynchQuery q = new OSQLSynchQuery(postQstr);

        List<ODocument> postsDoc = db.command(q).execute(OrientIdentityUtil.decode(topicId), type);
        List<Post> posts = PostMapper.buildPosts(postsDoc);
        return posts;
    }

    public List<Post> getPagedPostsByTopicIdAndType(String topicId, String type, int page, int size) {
        List<Post> posts = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            posts = getPagedPostsByTopicIdAndType(db, topicId, type, page, size);
        }
        finally {
            db.close();
        }

        return posts;
    }

    public List<Post> getPagedPostsByTopicIdAndType(OGraphDatabase db, String topicId, String type, int page, int size) {
        String postQstr = "select from Post where plan.topic in ? and type = ? order by createTime desc skip " + 
                           (page - 1) * size +
                           " limit " + size;
        OSQLSynchQuery q = new OSQLSynchQuery(postQstr);

        List<ODocument> postsDoc = db.command(q).execute(OrientIdentityUtil.decode(topicId), type);
        List<Post> posts = PostMapper.buildPosts(postsDoc);
        return posts;
    }

    public long getPostsCountByPlanId(String planId) {
        long result = 0;

        String sql = "select count(*) from Post where plan in ?";
        OGraphDatabase db = dataSource.getDB();
        try {
            OSQLSynchQuery q = new OSQLSynchQuery(sql);
            List<ODocument> postsCountDoc = db.command(q).execute(OrientIdentityUtil.decode(planId));
            if(postsCountDoc.size() > 0) {
                 ODocument doc = postsCountDoc.get(0);
                 result = doc.field("count", long.class);
            }
        }
        finally {
            db.close();
        }
        return result;
    }

    public List<Post> getPagedPostsByPlanId(String planId, int page, int size) {
        List<Post> posts = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            posts = getPagedPostsByPlanId(db, planId, page, size);
        }
        finally {
            db.close();
        }
        return posts;
    }

    public List<Post> getPagedPostsByPlanId(OGraphDatabase db, String planId, int page, int size) {
        String postQstr = "select from Post where plan in ? order by createTime desc skip " +
                           (page - 1) * size + 
                           " limit " + size;
        OSQLSynchQuery q = new OSQLSynchQuery(postQstr);
        List<ODocument> postsDoc = db.command(q).execute(OrientIdentityUtil.decode(planId));
        List<Post> posts = PostMapper.buildPosts(postsDoc);
        return posts;
    }

    public List<Post> getPostsByPlanId(String planId) {
        List<Post> posts = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            posts = getPostsByPlanId(db, planId);
        }
        finally {
            db.close();
        }
        return posts;
    }

    public List<Post> getPostsByPlanId(OGraphDatabase db, String planId) {
        String postQstr = "select from Post where plan in ?";
        OSQLSynchQuery q = new OSQLSynchQuery(postQstr);
        List<ODocument> postsDoc = db.command(q).execute(OrientIdentityUtil.decode(planId));
        List<Post> posts = PostMapper.buildPosts(postsDoc);
        return posts;
    }

    //-- by user
    
    public long getPostsCountByUserId(String userId) {
        long result = 0;

        String sql = "select count(*) from Post where user in ?";
        OGraphDatabase db = dataSource.getDB();
        try {
            OSQLSynchQuery q = new OSQLSynchQuery(sql);
            List<ODocument> postsCountDoc = db.command(q).execute(OrientIdentityUtil.decode(userId));
            if(postsCountDoc.size() > 0) {
                 ODocument doc = postsCountDoc.get(0);
                 result = doc.field("count", long.class);
            }
        }
        finally {
            db.close();
        }
        return result;
    }


    public List<Post> getPagedPostsByUserId(String userId, int page, int size) {
        List<Post> posts = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            posts = getPagedPostsByUserId(db, userId, page, size);
        }
        finally {
            db.close();
        }
        return posts;
    }

    public List<Post> getPagedPostsByUserId(OGraphDatabase db, String userId, int page, int size) {
        String postQstr = "select from Post where user in ? order by createTime desc skip " +
                           (page - 1) * size +
                           " limit " + size;
        OSQLSynchQuery q = new OSQLSynchQuery(postQstr);
        List<ODocument> postsDoc = db.command(q).execute(OrientIdentityUtil.decode(userId));
        List<Post> posts = PostMapper.buildPosts(postsDoc);
        return posts;
    }

    public List<Post> getPostsByUserId(String userId) {
        List<Post> posts = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            posts = getPostsByUserId(db, userId);
        }
        finally {
            db.close();
        }
        return posts;
    }

    public List<Post> getPostsByUserId(OGraphDatabase db, String userId) {
        String postQstr = "select from Post where user in ?";
        OSQLSynchQuery q = new OSQLSynchQuery(postQstr);
        List<ODocument> postsDoc = db.command(q).execute(OrientIdentityUtil.decode(userId));
        List<Post> posts = PostMapper.buildPosts(postsDoc);
        return posts;
    }

}
