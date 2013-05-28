package com.tissue.pipes;

import com.tissue.social.Activity;
import com.tissue.social.ActivityObject;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.pipes.PipeFunction;
import java.util.List;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnswerCommentPipeFunction extends ActivityPipeFunction  {
    
    private static Logger logger = LoggerFactory.getLogger(AnswerCommentPipeFunction.class);

    public AnswerCommentPipeFunction(List<Activity> activities) {
        super(activities);
    }

    public List<Activity> compute(ODocument doc) {
        String category = doc.field("category", String.class);

        if("answerComment".equals(category)) {
            Activity activity = init(doc);
            activity.setLabel(category);

            ODocument questionDoc = doc.field("what.out_CommentsAnswer.out_AnswersQuestion");
            activity.getWhat().setId(questionDoc.getIdentity().toString());

            String title = questionDoc.field("title", String.class);
            activity.getWhat().setDisplayName(title);

            activities.add(activity);
        }
        return null;
    }
}
