package com.tissue.core.command;

import com.tissue.core.plan.PostMessage;

public interface PostMessageCommentCommand extends ItemCommand {
    PostMessage getPostMessage();
}
