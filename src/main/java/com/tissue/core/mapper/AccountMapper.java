package com.tissue.core.mapper;

import com.tissue.core.Account;
import com.tissue.core.User;
import com.tissue.core.Node;
import com.tissue.core.UserGeneratedContent;
import com.tissue.core.command.UserCommand;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.nio.charset.Charset;
import com.google.common.hash.Hashing;

public class AccountMapper {

    public static ODocument convertAccount(UserCommand command) {
        ODocument doc = new ODocument("Account");
        doc.field("username", command.getAccount().getUsername());
        doc.field("password", Hashing.md5().hashString(command.getAccount().getPassword(), Charset.forName("utf-8")).toString());
        doc.field("email", command.getAccount().getEmail());
        doc.field("createTime", new Date());

        Set<String> roles = new HashSet();
        roles.add("ROLE_USER");
        if(command.getStatus() != null) {
            roles.add("ROLE_EVIL");
        }
        doc.field("roles", roles);

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

        Set<String> roles = doc.field("roles", Set.class);
        account.setRoles(roles);

        ODocument userDoc = doc.field("out_AccountsUser");
        User user = UserMapper.buildUser(userDoc);
        account.setUser(user);

        return account;
    }

    public static void setAccount(Node target, ODocument doc) {
        Date createTime = doc.field("out_Owner.createTime", Date.class);
        target.setCreateTime(createTime);

        ODocument accountDoc = doc.field("out_Owner.in");
        Account account = buildAccount(accountDoc);
        target.setAccount(account);
    }

}
