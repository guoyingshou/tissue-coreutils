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
        //String accountId;

        OrientGraph db = dataSource.getDB();
        try {

            Vertex v = db.addVertex("class:Account");
            v.setProperty("username", command.getAccount().getUsername());
            v.setProperty("password", Hashing.md5().hashString(command.getAccount().getPassword(), Charset.forName("utf-8")).toString());
            v.setProperty("email", command.getAccount().getEmail());
            v.setProperty("createTime", new Date());

            Set<String> roles = new HashSet();
            roles.add("ROLE_USER");
            v.setProperty("roles", roles);

            Vertex v2 = db.addVertex("class:User");
            v2.setProperty("displayName", command.getDisplayName());
            v2.setProperty("headline", command.getHeadline());
            v2.setProperty("inviteLimit", 32);
            //v2.setProperty("status", command.getStatus());
 
            db.addEdge("class:AccountUser", v, v2, "AccountUser");

            return v.getId().toString();

            /**
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
            */
        }
        finally {
           db.shutdown();
        }
        //return accountId;
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

        OrientGraph db = dataSource.getDB();
        try {
            Vertex v = db.getVertex(accountId);
            Account account = AccountMapper.buildAccount(v);

            String sql = "select flatten(out('AccountUser')) as user from " + accountId;
            Iterable<Vertex> vertices = db.command(new OSQLSynchQuery(sql)).execute();
            for(Vertex uv : vertices) {
                User user = UserMapper.buildUser(uv);
                account.setUser(user);
                break;
            }

            return account;
        }
        finally {
            db.shutdown();
        }

         /**
        String sql = "select from " + accountId;
        logger.debug(sql);

        Account account = null;
        OrientGraph db = dataSource.getDB();
        try {
            List<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                account = AccountMapper.buildAccount(doc);
            }
        }
        finally {
            db.shutdown();
        }
        return account;
        */
    }

    public Account getAccountByEmail(String email) {
        String sql = "select from account where email = '" + email + "'";
        logger.debug(sql);

        Account account = null;

        OrientGraph db = dataSource.getDB();
        try {
            List<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                account = AccountMapper.buildAccount(doc);
            }
        }
        finally {
            db.shutdown();
        }
        return account;
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
                    
            /**
            List<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            if(docs.size() > 0) {
               exist = true;
            }
            */
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

             /**
            List<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            if(docs.size() > 0) {
               exist = true;
            }
            */
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
