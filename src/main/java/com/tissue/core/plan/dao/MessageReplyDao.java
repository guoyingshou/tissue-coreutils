package com.tissue.core.plan.dao;

import com.tissue.core.command.MessageReplyCommand;
import com.tissue.core.plan.MessageReply;

public interface MessageReplyDao {

    String create(MessageReplyCommand command);

    void update(MessageReplyCommand command);
 
}
