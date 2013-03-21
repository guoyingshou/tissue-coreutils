package com.tissue.core.mapper;

import com.tissue.core.Verification;
import com.tissue.core.Account;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class VerificationMapper {

    public static Verification buildVerification(ODocument doc) {
        Verification verification = new Verification();
        verification.setId(doc.getIdentity().toString());

        String code = doc.field("code", String.class);
        verification.setCode(code);

        ODocument accountDoc = doc.field("account");
        Account account = AccountMapper.buildAccount(accountDoc);
        verification.setAccount(account);

        return verification;
    }
}
