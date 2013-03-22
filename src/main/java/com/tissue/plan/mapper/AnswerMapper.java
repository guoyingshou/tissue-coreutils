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
        return doc;
    }

    public static Answer buildAnswer(ODocument answerDoc) {
        Answer answer = new Answer();
        answer.setId(answerDoc.getIdentity().toString());

        String answerContent = answerDoc.field("content", String.class);
        answer.setContent(answerContent);

        Set<ODocument> inEdges = answerDoc.field("in");
        for(ODocument inEdge : inEdges) {
            String label = inEdge.field("label", String.class);
            if("answer".equals(label)) {
                Date createTime = inEdge.field("createTime", Date.class);
                answer.setCreateTime(createTime);

                ODocument accountDoc = inEdge.field("out");
                Account account = AccountMapper.buildAccount(accountDoc);
                answer.setAccount(account);
                break;
            }
        }
        return answer;
    }

}