package com.tissue.core.dao;

import com.tissue.core.About;
import com.tissue.core.command.CommentCommand;
import java.util.List;

public interface AboutDao {

    String addAbout(CommentCommand command);

    List<About> getAbouts();

}
