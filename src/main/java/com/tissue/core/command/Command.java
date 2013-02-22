package com.tissue.core.command;

import com.tissue.core.command.ItemCommand;
import com.tissue.core.social.Account;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

public class Command implements ItemCommand, Serializable {

    private String id;

    @NotNull
    @NotEmpty
    private String content;

    private Account account;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }
}
