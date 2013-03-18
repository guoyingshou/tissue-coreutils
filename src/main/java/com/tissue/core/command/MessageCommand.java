package com.tissue.core.command;

import com.tissue.core.plan.Article;

public interface MessageCommand extends ContentCommand {
    Article getArticle();
}
