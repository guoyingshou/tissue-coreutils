package com.tissue.core.plan.dao.orient;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.converter.PostConverter;
import com.tissue.core.profile.User;
import com.tissue.core.plan.Question;
import com.tissue.core.plan.dao.QuestionDao;

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
public class QuestionDaoImpl implements QuestionDao {

    @Autowired
    private OrientDataSource dataSource;

    public Question create(Question question) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = PostConverter.convert(question);
            doc.save();

            String ridQuestion = doc.getIdentity().toString();
            String ridUser = OrientIdentityUtil.decode(question.getUser().getId());

            String sql = "create edge EdgeQuestion from " + 
                          ridUser + 
                          " to " + 
                          ridQuestion + 
                          " set label = 'question', createTime = sysdate()";

            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            String questionId = OrientIdentityUtil.encode(ridQuestion);
            question.setId(questionId);
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return question;
    }

}
