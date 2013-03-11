package com.tissue.core.social.dao;

import com.tissue.core.command.ResetRequestCommand;
import com.tissue.core.command.ResetPasswordCommand;

public interface ResetDao {

    String create(ResetRequestCommand command);

    boolean isEmailExist(String email);

    boolean isCodeExist(String code);

    String getEmail(String code);

    void updatePassword(ResetPasswordCommand command);

}
