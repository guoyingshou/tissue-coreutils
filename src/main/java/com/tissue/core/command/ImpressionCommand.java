package com.tissue.core.command;

//import com.tissue.core.social.Impression;
import com.tissue.core.social.Account;

public interface ImpressionCommand {
    //Impression getImpression();
    String getContent();
    String getUserId();
    Account getAccount();
}
