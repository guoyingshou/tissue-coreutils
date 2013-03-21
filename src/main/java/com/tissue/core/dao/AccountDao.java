package com.tissue.core.dao;

import com.tissue.core.Account;
import com.tissue.core.command.UserCommand;
import com.tissue.core.command.EmailCommand;
import com.tissue.core.command.PasswordCommand;
import java.util.List;

public interface AccountDao {

    boolean isUsernameExist(String username);

    boolean isEmailExist(String email);

    String create(UserCommand userCommand);

    void updateEmail(EmailCommand command);

    void updatePassword(PasswordCommand command);

    Account getAccount(String accountId);

    Account getAccountByEmail(String email);

    void setVerified(String accountId);
}
