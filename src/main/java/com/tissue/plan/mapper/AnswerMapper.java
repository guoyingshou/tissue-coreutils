package com.tissue.plan.mapper;

import com.tissue.core.Account;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.plan.command.AnswerCommand;
import com.tissue.plan.Answer;
import com.tissue.plan.AnswerComment;
import com.tissue.plan.Question;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class AnswerMapper {

    public static ODocument convertAnswer(AnswerCommand answer) {
        ODocument doc = new ODocument("Answer");
        doc.field("content", answer.getContent());
        doc.field("type", "answer");
        return doc;
    }

    public static Answer buildAnswer(ODocument doc) {
        Answer answer = new Answer();
        answer.setId(doc.getIdentity().toString());

        String answerContent = doc.field("content", String.class);
        answer.setContent(answerContent);

        AccountMapper.setAccount(answer, doc);

        return answer;
    }

}
