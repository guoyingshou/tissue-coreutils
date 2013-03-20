package com.tissue.plan;

import com.tissue.core.UserGeneratedContent2;
import com.tissue.core.Account;
import com.tissue.core.TimeFormat;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class Post extends UserGeneratedContent2 {

    private Plan plan;

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Plan getPlan() {
        return plan;
    }

}
