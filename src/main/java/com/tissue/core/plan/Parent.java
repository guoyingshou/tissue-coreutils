package com.tissue.core.plan;

import com.tissue.core.util.TimeFormat;
import com.tissue.core.social.Account;

import org.joda.time.DateTime;
import org.joda.time.Period;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class Parent implements Serializable {

    protected String id;

    protected Date createTime;

    protected Account account;
   
    private boolean deleted = false;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public boolean isOwner(String viewerId) {
        if((viewerId != null) && viewerId.equals(account.getId())) {
            return true;
        }
        return false;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public TimeFormat getTimeBefore() {
        return new TimeFormat() {
            public int getYears() {
                return getPeriod().getYears();
            }

            public int getMonths() {
                return getPeriod().getMonths();
            }

            public int getWeeks() {
                return getPeriod().getWeeks();
            }

            public int getDays() {
                return getPeriod().getDays();
            }

            public int getHours() {
                return getPeriod().getHours();
            }

            public int getMinutes() {
                return getPeriod().getMinutes();
            }

            public int getSeconds() {
                return getPeriod().getSeconds();
            }
        };
    }

    private Period getPeriod() {
        DateTime start = new DateTime(getCreateTime());
        DateTime now = new DateTime();

        Period p = new Period(start, now);
        return p;
    }

}
