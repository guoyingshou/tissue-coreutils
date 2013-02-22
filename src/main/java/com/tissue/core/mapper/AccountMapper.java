package com.tissue.core.mapper;

import com.tissue.core.command.UserCommand;
import com.tissue.core.social.Account;
import com.tissue.core.social.User;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class AccountMapper {

    public static ODocument convertAccount(UserCommand userCommand) {
        ODocument doc = new ODocument("Account");
        doc.field("username", userCommand.getUsername());
        doc.field("password", userCommand.getPassword());
        doc.field("email", userCommand.getEmail());
        return doc;
    }

    public static Account buildAccount(ODocument doc) {
        Account account = new Account();
        String rid = doc.getIdentity().toString();
        account.setId(rid);

        String username = doc.field("username", String.class);
        account.setUsername(username);

        String password = doc.field("password", String.class);
        account.setPassword(password);

        String email = doc.field("email", String.class);
        account.setEmail(email);

        ODocument userDoc = doc.field("user");
        User user = UserMapper.buildUserSelf(userDoc);
        account.setUser(user);

        return account;
    }
}
