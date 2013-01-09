package com.tissue.core.plan.dao.orient;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.converter.PostMessageCommentConverter;
import com.tissue.core.profile.User;
import com.tissue.core.plan.PostMessage;
import com.tissue.core.plan.PostMessageComment;
import com.tissue.core.plan.dao.PostMessageCommentDao;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;

@Component
public class PostMessageCommentDaoImpl implements PostMessageCommentDao {

    @Autowired
    private OrientDataSource dataSource;

    public PostMessageComment create(PostMessageComment comment) {

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument commentDoc = PostMessageCommentConverter.convertPostMessageComment(comment);
            commentDoc.save();

            String ridComment = commentDoc.getIdentity().toString();
            String ridUser = OrientIdentityUtil.decode(comment.getUser().getId());

            String sql = "create edge EdgePostMessageComment from " + ridUser + " to " + ridComment + " set label = 'postMessageComment', createTime = sysdate()";
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            String ridPostMessage = OrientIdentityUtil.decode(comment.getPostMessage().getId());
            String sql2 = "update " + ridPostMessage + " add comments = " + ridComment;
            cmd = new OCommandSQL(sql2);
            db.command(cmd).execute();

            comment.setId(OrientIdentityUtil.encode(ridComment));
        }
        finally {
            db.close();
        }

        return comment;
    }

    public PostMessageComment update(PostMessageComment comment) {
        String ridComment = OrientIdentityUtil.decode(comment.getId());
        String sql = "update " + ridComment + " set content = '" + comment.getContent() + "'";

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }

        return comment;
    }

    public void delete(String commentId) {
        String ridComment = OrientIdentityUtil.decode(commentId);
        String sql = "update " + ridComment + " set status = 'deleted'";

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
