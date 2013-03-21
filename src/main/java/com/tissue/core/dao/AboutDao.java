package com.tissue.core.dao;

import com.tissue.core.About;
import com.tissue.core.command.ContentCommand;
import java.util.List;

public interface AboutDao extends ContentDao {
    String create(ContentCommand command);
    List<About> getAbouts();
}
