package com.tissue.core.plan.dao;

import com.tissue.core.dao.ContentDao;
import com.tissue.core.plan.command.MessageCommand;
import com.tissue.core.plan.Message;
import java.util.List;

public interface MessageDao extends ContentDao {

    String create(MessageCommand command);

    Message getMessage(String messageId);

}
