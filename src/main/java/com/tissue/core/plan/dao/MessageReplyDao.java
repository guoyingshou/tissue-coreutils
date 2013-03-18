package com.tissue.core.plan.dao;

import com.tissue.core.command.MessageReplyCommand;
import com.tissue.core.plan.MessageReply;

public interface MessageReplyDao {

    String create(MessageReplyCommand command);

    MessageReply getMessageReply(String messageReplyId);

    //void update(MessageReplyCommand command);
 
}
