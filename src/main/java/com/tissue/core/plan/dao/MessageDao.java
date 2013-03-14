package com.tissue.core.plan.dao;

import com.tissue.core.plan.Message;
import com.tissue.core.command.MessageCommand;
import java.util.List;

public interface MessageDao {

    String create(MessageCommand command);

    void update(MessageCommand command);


}
