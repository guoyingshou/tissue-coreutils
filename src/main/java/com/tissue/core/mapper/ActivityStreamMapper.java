package com.tissue.core.mapper;

import com.tissue.core.social.Activity;
import com.tissue.core.pipes.TopicPipeFunction;
import com.tissue.core.pipes.PlanPipeFunction;
import com.tissue.core.pipes.PostPipeFunction;
import com.tissue.core.pipes.MessagePipeFunction;
import com.tissue.core.pipes.MessageReplyPipeFunction;
import com.tissue.core.pipes.QuestionCommentPipeFunction;
import com.tissue.core.pipes.AnswerPipeFunction;
import com.tissue.core.pipes.AnswerCommentPipeFunction;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.pipes.util.Pipeline;
import com.tinkerpop.pipes.sideeffect.SideEffectFunctionPipe;

import java.util.List;
import java.util.ArrayList;

public class ActivityStreamMapper {

    private List<Activity> activities;

    private Pipeline<ODocument, ODocument> pipeline = new Pipeline();

    public ActivityStreamMapper() {
        this.activities = new ArrayList();

        SideEffectFunctionPipe topicPipe = new SideEffectFunctionPipe<ODocument>(new TopicPipeFunction(activities));
        pipeline.addPipe(topicPipe);

        SideEffectFunctionPipe planPipe = new SideEffectFunctionPipe<ODocument>(new PlanPipeFunction(activities));
        pipeline.addPipe(planPipe);

        SideEffectFunctionPipe postPipe = new SideEffectFunctionPipe<ODocument>(new PostPipeFunction(activities));
        pipeline.addPipe(postPipe);

        SideEffectFunctionPipe messagePipe = new SideEffectFunctionPipe<ODocument>(new MessagePipeFunction(activities));
        pipeline.addPipe(messagePipe);

        SideEffectFunctionPipe messageReplyPipe = new SideEffectFunctionPipe<ODocument>(new MessageReplyPipeFunction(activities));
        pipeline.addPipe(messageReplyPipe);

        SideEffectFunctionPipe questionCommentPipe = new SideEffectFunctionPipe<ODocument>(new QuestionCommentPipeFunction(activities));
        pipeline.addPipe(questionCommentPipe);

        SideEffectFunctionPipe answerPipe = new SideEffectFunctionPipe<ODocument>(new AnswerPipeFunction(activities));
        pipeline.addPipe(answerPipe);

        SideEffectFunctionPipe answerCommentPipe = new SideEffectFunctionPipe<ODocument>(new AnswerCommentPipeFunction(activities));
        pipeline.addPipe(answerCommentPipe);

    }

    public List<Activity> process(List<ODocument> docs) {
        pipeline.setStarts(docs);
        while(pipeline.hasNext()) {
            pipeline.next();
        }
        return activities;
    }

}
