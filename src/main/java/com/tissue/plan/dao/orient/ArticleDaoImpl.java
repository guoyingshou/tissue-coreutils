package com.tissue.plan.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.User;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.core.mapper.UserMapper;
import com.tissue.core.dao.orient.ContentDaoImpl;
import com.tissue.plan.dao.ArticleDao;
import com.tissue.plan.mapper.TopicMapper;
import com.tissue.plan.mapper.PlanMapper;
import com.tissue.plan.mapper.ArticleMapper;
import com.tissue.plan.mapper.MessageMapper;
import com.tissue.plan.mapper.MessageReplyMapper;
import com.tissue.plan.Topic;
import com.tissue.plan.Plan;
import com.tissue.plan.Article;
import com.tissue.plan.Message;
import com.tissue.plan.MessageReply;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;


import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class ArticleDaoImpl extends PostDaoImpl implements ArticleDao {

    private static Logger logger = LoggerFactory.getLogger(ArticleDaoImpl.class);

    public Article getArticle(String id) {
        String sql = "select @this as article, " + 
                     "out_PostAccount.createTime as createTime, " +
                     "out_PostAccount.in as account, " + 
                     "out_PostAccount.in.out_AccountsUser as user, " + 
                     "out_PostsPlan as plan, " + 
                     "out_PostsPlan.out_PlansTopic as topic, " + 
                     "out('PostsPlan').out('PlansTopic').in('PlansTopic') as topicPlans, " +
                     "in('MessagesArticle') as messages " + 
                     "from " + id;

        logger.debug(sql);

        Article article = null;

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:4")).execute();
            for(ODocument doc : docs) {
                ODocument articleDoc = doc.field("article");
                article = ArticleMapper.buildArticle(articleDoc);

                Date createTime = doc.field("createTime", Date.class);
                article.setCreateTime(createTime);

                ODocument accountDoc = doc.field("account");
                Account account = AccountMapper.buildAccount(accountDoc);
                article.setAccount(account);

                ODocument userDoc = doc.field("user");
                User user = UserMapper.buildUser(userDoc);
                account.setUser(user);

                ODocument planDoc = doc.field("plan");
                Plan plan = PlanMapper.buildPlan(planDoc);
                article.setPlan(plan);

                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                plan.setTopic(topic);

                List<ODocument> topicPlanDocs = doc.field("topicPlans");
                for(ODocument topicPlanDoc : topicPlanDocs) {
                    Plan topicPlan = PlanMapper.buildPlan(topicPlanDoc);
                    topic.addPlan(topicPlan);

                    ODocument topicPlanAccountDoc = topicPlanDoc.field("out_PlanAccount.in");
                    Account topicPlanAccount = AccountMapper.buildAccount(topicPlanAccountDoc);
                    topicPlan.setAccount(topicPlanAccount);

                    ODocument topicPlanUserDoc = topicPlanAccountDoc.field("out_AccountsUser");
                    User topicPlanUser = UserMapper.buildUser(topicPlanUserDoc);
                    topicPlanAccount.setUser(topicPlanUser);
                }

                List<ODocument> messageDocs = doc.field("messages");
                for(ODocument messageDoc : messageDocs) {
                    Object deleted = messageDoc.field("deleted");
                    if(deleted == null) {
                        Message message = MessageMapper.buildMessage(messageDoc);
                        article.addMessage(message);

                        Date messageCreateTime = messageDoc.field("out_PostAccount.createTime", Date.class);
                        message.setCreateTime(messageCreateTime);

                        ODocument messageAccountDoc = messageDoc.field("out_PostAccount.in");
                        Account messageAccount = AccountMapper.buildAccount(messageAccountDoc);
                        message.setAccount(messageAccount);

                        ODocument messageUserDoc = messageAccountDoc.field("out_AccountsUser");
                        User messageUser = UserMapper.buildUser(messageUserDoc);
                        messageAccount.setUser(messageUser);

                        Set<ODocument> replyDocs = new HashSet<ODocument>();
                        Object obj = messageDoc.field("in_RepliesMessage");
                        if(obj != null) {
                            if(obj instanceof ODocument) {
                                replyDocs.add((ODocument)obj);
                            }
                            else {
                                replyDocs = (Set)obj;
                            }

                            for(ODocument replyDoc : replyDocs) {
                                deleted = replyDoc.field("deleted");
                                if(deleted == null) {
                                    MessageReply reply = MessageReplyMapper.buildMessageReply(replyDoc);
                                    message.addReply(reply);

                                    Date replyCreateTime = replyDoc.field("out_PostAccount.createTime");
                                    reply.setCreateTime(replyCreateTime);
                                
                                    ODocument replyAccountDoc = replyDoc.field("out_PostAccount.in");
                                    Account replyAccount = AccountMapper.buildAccount(replyAccountDoc);
                                    reply.setAccount(replyAccount);

                                    ODocument replyUserDoc = replyAccountDoc.field("out_AccountsUser");
                                    User replyUser = UserMapper.buildUser(replyUserDoc);
                                    replyAccount.setUser(replyUser);
                                }
                            }
                        }
                    }
                }
                
                /**
                List<ODocument> messagesDoc = articleDoc.field("messages");
                if(messagesDoc != null) {
                    for(ODocument messageDoc : messagesDoc) {
                        Object deleted = messageDoc.field("deleted");
                        if(deleted == null) {
                            Message message = MessageMapper.buildMessage(messageDoc);
                            //AccountMapper.setupCreatorAndTimestamp(message, messageDoc);
                            article.addMessage(message);

                            List<ODocument> repliesDoc = messageDoc.field("replies");
                            if(repliesDoc != null) {
                                for(ODocument replyDoc : repliesDoc) {
                                    deleted = replyDoc.field("deleted", String.class);
                                    if(deleted == null) {
                                        MessageReply reply = MessageReplyMapper.buildMessageReply(replyDoc);
                                        //AccountMapper.setupCreatorAndTimestamp(reply, replyDoc);
                                        message.addReply(reply);
                                    }
                                }
                            }
                        }
                    }
                }
                */
            }
        }
        finally {
            db.shutdown();
        }
        return article;
    }

}
