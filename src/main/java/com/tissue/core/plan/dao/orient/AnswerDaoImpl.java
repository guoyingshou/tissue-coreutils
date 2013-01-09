package com.tissue.core.plan.dao.orient;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.mapper.AnswerMapper;
import com.tissue.core.profile.User;
import com.tissue.core.plan.Post;
import com.tissue.core.plan.Answer;
import com.tissue.core.plan.dao.AnswerDao;

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
public class AnswerDaoImpl implements AnswerDao {

    @Autowired
    private OrientDataSource dataSource;

    /**
     * It seems that sql command cann't be mixed with java api call.
     */
    public Answer create(Answer answer) {

        OGraphDatabase db = dataSource.getDB();
        try {

            ODocument doc = AnswerMapper.convertAnswer(answer);
            doc.save();

            String ridAnswer = doc.getIdentity().toString();
            String ridUser = OrientIdentityUtil.decode(answer.getUser().getId());

            String sql = "create edge EdgeAnswer from " + ridUser + " to " + ridAnswer + " set label = 'answer', createTime = sysdate()";
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            String ridQuestion = OrientIdentityUtil.decode(answer.getQuestion().getId());

            String sql2 = "update " + ridQuestion + " add answers = " + ridAnswer;
            cmd = new OCommandSQL(sql2);
            db.command(cmd).execute();

            answer.setId(OrientIdentityUtil.encode(ridAnswer));
        }
        finally {
            db.close();
        }

        return answer;
    }

    public void update(Answer answer) {
        String ridAnswer = OrientIdentityUtil.decode(answer.getId());
        String sql = "update " + ridAnswer + " set content = '" + answer.getContent() + "'";

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
    }

    public void delete(String answerId) {
        String ridAnswer = OrientIdentityUtil.decode(answerId);
        String sql = "update " + ridAnswer + " set status = 'deleted'";

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
