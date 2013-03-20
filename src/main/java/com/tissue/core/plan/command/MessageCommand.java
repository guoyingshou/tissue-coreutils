package com.tissue.core.plan.command;

import com.tissue.core.command.ContentCommand;
import com.tissue.core.plan.Article;

public interface MessageCommand extends ContentCommand {
    Article getArticle();
}
