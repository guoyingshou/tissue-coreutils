package com.tissue.core.plan.dao.orient;

import com.tissue.core.command.AnswerCommentCommand;
import com.tissue.core.util.OrientDataSource;

import com.tissue.core.mapper.AnswerCommentMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Answer;
import com.tissue.core.plan.AnswerComment;
import com.tissue.core.plan.dao.AnswerCommentDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AnswerCommentDaoImpl implements AnswerCommentDao {

    private static Logger logger = LoggerFactory.getLogger(AnswerCommentDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public String create(AnswerCommentCommand command) {
        String id = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = AnswerCommentMapper.convertAnswerComment(command);
            db.save(doc);

            id = doc.getIdentity().toString();
            String userId = command.getAccount().getId();
            String answerId = command.getAnswer().getId();

            String sql = "create edge EdgePost from " + userId + " to " + id + " set label = 'answerComment', createTime = sysdate()";
            logger.debug(sql);

            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + answerId + " add comments = " + id;
            logger.debug(sql);

            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
             db.close();
        }

        return id;
    }

    public void update(AnswerCommentCommand command) {
        String sql = "update " + command.getId() + " set content = '" + command.getContent() + "'";
        logger.debug(sql);

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
