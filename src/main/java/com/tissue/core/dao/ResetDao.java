package com.tissue.core.dao;

import com.tissue.core.Reset;
import com.tissue.core.command.ResetCommand;

public interface ResetDao {

    String create(ResetCommand command);

    Reset getReset(String code);

    void delete(String resetId);

}
