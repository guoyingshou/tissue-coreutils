package com.tissue.plan.command;

import com.tissue.core.command.ContentCommand;
import com.tissue.plan.Article;

public interface MessageCommand extends ContentCommand {
    Article getArticle();
}
