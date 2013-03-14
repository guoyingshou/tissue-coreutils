package com.tissue.core.plan.dao.orient;

import com.tissue.core.command.PostCommand;
import com.tissue.core.util.OrientDataSource;

import com.tissue.core.mapper.PostMapper;
import com.tissue.core.plan.Post;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PostDaoImpl implements PostDao {

    private static Logger logger = LoggerFactory.getLogger(PostDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public List<Post> getLatestPosts(int limit) {
        List<Post> posts = new ArrayList();

        String sql = "select from Post where deleted is null and plan.topic.deleted is null and type contains ['concept', 'note', 'tutorial'] order by createTime desc limit " + limit;
        logger.debug(sql);

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

}
