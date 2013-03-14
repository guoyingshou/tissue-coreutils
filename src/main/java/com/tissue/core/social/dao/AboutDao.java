package com.tissue.core.social.dao;

import com.tissue.core.command.CommentCommand;
import com.tissue.core.social.About;
import java.util.List;

public interface AboutDao {

    String addAbout(CommentCommand command);

    List<About> getAbouts();

}
