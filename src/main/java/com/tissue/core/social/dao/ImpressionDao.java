package com.tissue.core.social.dao;

import com.tissue.core.command.ImpressionCommand;
import com.tissue.core.social.Impression;
import java.util.List;

public interface ImpressionDao {

    void addImpression(ImpressionCommand command);

    List<Impression> getImpressions(String userId);

}
