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
import java.nio.charset.Charset;
import com.google.common.hash.Hashing;

public class AccountMapper {

    public static ODocument convertAccount(UserCommand command) {
        ODocument doc = new ODocument("Account");
        doc.field("username", command.getUsername());
        doc.field("password", Hashing.md5().hashString(command.getPassword(), Charset.forName("utf-8")).toString());
        doc.field("email", command.getEmail());
        return doc;
    }

    public static Account buildAccount(ODocument doc) {
        Account account = new Account();
        String rid = doc.getIdentity().toString();
        account.setId(rid);

        String username = doc.field("username", String.class);
        account.setUsername(username);

        String email = doc.field("email", String.class);
        account.setEmail(email);

        ODocument userDoc = doc.field("user");
        User user = UserMapper.buildUserSelf(userDoc);
        account.setUser(user);

        return account;
    }
}
