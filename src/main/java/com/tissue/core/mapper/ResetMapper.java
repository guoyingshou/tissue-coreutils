package com.tissue.core.mapper;

import com.tissue.core.Reset;
import com.tissue.core.Account;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ResetMapper {

    public static Reset buildReset(ODocument doc) {
        Reset reset = new Reset();
        reset.setId(doc.getIdentity().toString());

        String code = doc.field("code", String.class);
        reset.setCode(code);

        ODocument accountDoc = doc.field("account");
        Account account = AccountMapper.buildAccount(accountDoc);
        reset.setAccount(account);

        return reset;
    }
}
