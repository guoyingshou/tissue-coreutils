package com.tissue.plan.dao;

import com.tissue.core.dao.ContentDao;
import com.tissue.plan.command.MessageReplyCommand;
import com.tissue.plan.MessageReply;

public interface MessageReplyDao extends ContentDao {

    String create(MessageReplyCommand command);

    MessageReply getMessageReply(String messageReplyId);

}
