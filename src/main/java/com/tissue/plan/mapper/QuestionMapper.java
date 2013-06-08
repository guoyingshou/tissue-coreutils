package com.tissue.plan.mapper;

import com.tissue.core.Account;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.plan.command.PostCommand;
import com.tissue.plan.Question;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class QuestionMapper {

    public static Question buildQuestion(ODocument doc) {
        Question q = new Question();
        q.setId(doc.getIdentity().toString());

        String title = doc.field("title", String.class);
        q.setTitle(title);

        String content = doc.field("content", String.class);
        q.setContent(content);

        String type = doc.field("type", String.class);
        q.setType(type);

        Boolean deleted = doc.field("deleted", Boolean.class);
        if(deleted != null) {
            q.setDeleted(deleted);
        }
 
        AccountMapper.setAccount(q, doc);
        return q;
    }

}
