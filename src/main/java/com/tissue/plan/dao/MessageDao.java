package com.tissue.plan.dao;

import com.tissue.core.dao.ContentDao;
import com.tissue.plan.command.MessageCommand;
import com.tissue.plan.Message;
import java.util.List;

public interface MessageDao extends ContentDao {

    String create(MessageCommand command);

    Message getMessage(String messageId);

}
