package com.tissue.core;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.security.AccessControlException;

public class UserGeneratedContent extends Node {

    protected String content;

    public void setContent(String content) {
        this.content = content.trim();
    }

    public String getContent() {
        return content;
    }
}
