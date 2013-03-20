package com.tissue.social.dao;

import com.tissue.social.command.ImpressionCommand;
import com.tissue.social.Impression;
import java.util.List;

public interface ImpressionDao {

    void create(ImpressionCommand command);

    List<Impression> getImpressions(String userId);

}
