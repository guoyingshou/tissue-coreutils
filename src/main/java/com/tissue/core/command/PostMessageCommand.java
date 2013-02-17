package com.tissue.core.command;

import com.tissue.core.plan.Post;

public interface PostMessageCommand extends ItemCommand {
    Post getPost();
}
