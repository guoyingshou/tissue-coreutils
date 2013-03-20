package com.tissue.core.social.dao;

import com.tissue.core.social.command.ImpressionCommand;
import com.tissue.core.social.Impression;
import java.util.List;

public interface ImpressionDao {

    void create(ImpressionCommand command);

    List<Impression> getImpressions(String userId);

}
