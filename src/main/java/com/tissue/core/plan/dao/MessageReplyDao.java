package com.tissue.core.plan.dao;

import com.tissue.core.dao.ContentDao;
import com.tissue.core.plan.command.MessageReplyCommand;
import com.tissue.core.plan.MessageReply;

public interface MessageReplyDao extends ContentDao {

    String create(MessageReplyCommand command);

    MessageReply getMessageReply(String messageReplyId);

}
