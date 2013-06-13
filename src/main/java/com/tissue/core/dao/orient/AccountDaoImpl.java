package com.tissue.core.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.User;
import com.tissue.core.datasources.OrientDataSource;
import com.tissue.core.dao.AccountDao;
import com.tissue.core.command.UserCommand;
import com.tissue.core.command.EmailCommand;
import com.tissue.core.command.PasswordCommand;
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

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.Vertex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.nio.charset.Charset;
import com.google.common.hash.Hashing;

@Repository
public class AccountDaoImpl implements AccountDao {

    private static Logger logger = LoggerFactory.getLogger(AccountDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public String create(UserCommand command) {
        OrientGraph db = dataSource.getDB();
        try {
            ODocument accountDoc = AccountMapper.convertAccount(command);
            accountDoc.save();
            String accountId = accountDoc.getIdentity().toString();

            ODocument userDoc = UserMapper.convertUser(command);
            userDoc.save();
            String userId = userDoc.getIdentity().toString();

            String sql = "create Edge Belongs from " + accountId + " to " + userId;
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            return accountId;
        }
        finally {
           db.shutdown();
        }
    }
 
    public void updateEmail(EmailCommand command) {
        String sql = "update " + command.getAccount().getId() + " set email = '" + command.getEmail() + "'";
        logger.debug(sql);

        OrientGraph db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
           db.shutdown();
        }
    }

    public void updatePassword(PasswordCommand command) {
        String password = Hashing.md5().hashString(command.getPassword(), Charset.forName("utf-8")).toString();
        String sql = "update " + command.getAccount().getId() + " set password = '" + password + "'";
        logger.debug(sql);

        OrientGraph db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
           db.shutdown();
        }
    }

    public Account getAccount(String accountId) {

        String sql = "select @this as account from " + accountId;
        OrientGraph db = dataSource.getDB();
        try {
            Account account = null;
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql)).execute();
            for(ODocument doc : docs) {
                ODocument accountDoc = doc.field("account");
                account = AccountMapper.buildAccount(accountDoc);

                break;
            }
            return account;
        }
        finally {
            db.shutdown();
        }
    }

    public Account getAccountByEmail(String email) {
        String sql = "select from account where email = '" + email + "'";
        logger.debug(sql);

        OrientGraph db = dataSource.getDB();
        try {
            Account account = null;
            Iterable<ODocument> docs = db.getRawGraph().command(new OSQLSynchQuery(sql)).execute();
            for(ODocument doc : docs) {
                account = AccountMapper.buildAccount(doc);
                break;
            }
            return account;
        }
        finally {
            db.shutdown();
        }
    }

    public boolean isUsernameExist(String username) {
        String sql = "select from account where username = '" + username + "'";
        logger.debug(sql);

        boolean exist = false;

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<Vertex> vertices = db.command(new OSQLSynchQuery(sql)).execute();
            for(Vertex v : vertices) {
                exist = true;
                break;
            }
        }
        finally {
            db.shutdown();
        }
        return exist;
    }

    public boolean isEmailExist(String email) {
        String sql = "select from account where email = '" + email + "'";
        logger.debug(sql);

        boolean exist = false;

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<Vertex> vertices = db.command(new OSQLSynchQuery(sql)).execute();
            for(Vertex v : vertices) {
                exist = true;
                break;
            }
        }
        finally {
            db.shutdown();
        }
        return exist;
    }

    public void setVerified(String accountId) {
        String sql = "update " + accountId + " set verified = true";
        logger.debug(sql);

        OrientGraph db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.shutdown();
        }
    }

}
