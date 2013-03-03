package com.tissue.core.social.dao;

import com.tissue.core.command.AboutCommand;
import com.tissue.core.social.About;
import java.util.List;

public interface AboutDao {

    String addAbout(AboutCommand command);

    List<About> getAbouts();

}
