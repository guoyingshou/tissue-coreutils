package com.tissue.core.plan.dao;

import com.tissue.core.command.ContentCommand;

public interface ContentDao {

    void update(ContentCommand command);

    void delete(String rid);

}
