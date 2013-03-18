package com.tissue.core.plan.dao;

import com.tissue.core.plan.Message;
import com.tissue.core.command.MessageCommand;
import java.util.List;

public interface MessageDao {

    String create(MessageCommand command);

    Message getMessage(String messageId);

    //void update(MessageCommand command);


}
