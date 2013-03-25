package com.tissue.core.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.dao.AccountDao;
import com.tissue.core.command.UserCommand;
import com.tissue.core.command.EmailCommand;
import com.tissue.core.command.PasswordCommand;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.core.mapper.UserMapper;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.nio.charset.Charset;
import com.google.common.hash.Hashing;

@Repository
public class AccountDaoImpl implements AccountDao {

    private static Logger logger = LoggerFactory.getLogger(AccountDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public String create(UserCommand command) {
        String accountId;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument accountDoc = AccountMapper.convertAccount(command);
            accountDoc.save();

            ODocument userDoc = UserMapper.convertUser(command);
            List accounts = new ArrayList();
            accounts.add(accountDoc.getIdentity());
            userDoc.field("accounts", accounts);
            userDoc.save();

            accountDoc.field("user", userDoc.getIdentity());
            accountDoc.save();

            accountId = accountDoc.getIdentity().toString();
        }
        finally {
           db.close();
        }
        return accountId;
    }
 
    public void updateEmail(EmailCommand command) {
        String sql = "update " + command.getAccount().getId() + " set email = '" + command.getEmail() + "'";
        logger.debug(sql);

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
           db.close();
        }
    }

    public void updatePassword(PasswordCommand command) {
        String password = Hashing.md5().hashString(command.getPassword(), Charset.forName("utf-8")).toString();
        String sql = "update " + command.getAccount().getId() + " set password = '" + password + "'";
        logger.debug(sql);

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
           db.close();
        }
    }

    public Account getAccount(String accountId) {
        String sql = "select from " + accountId;
        logger.debug(sql);

        Account account = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                account = AccountMapper.buildAccount(doc);
            }
        }
        finally {
            db.close();
        }
        return account;
    }

    public Account getAccountByEmail(String email) {
        String sql = "select from account where email = '" + email + "'";
        logger.debug(sql);

        Account account = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                account = AccountMapper.buildAccount(doc);
            }
        }
        finally {
            db.close();
        }
        return account;
    }

    public boolean isUsernameExist(String username) {
        String sql = "select from account where username = '" + username + "'";
        logger.debug(sql);

        boolean exist = false;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(docs.size() > 0) {
               exist = true;
            }
        }
        finally {
            db.close();
        }
        return exist;
    }

    public boolean isEmailExist(String email) {
        String sql = "select from account where email = '" + email + "'";
        logger.debug(sql);

        boolean exist = false;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(docs.size() > 0) {
               exist = true;
            }
        }
        finally {
            db.close();
        }
        return exist;
    }

    public void setVerified(String accountId) {
        String sql = "update " + accountId + " set verified = true";
        logger.debug(sql);

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
    }

}
