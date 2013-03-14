package com.tissue.core.orient.dao;

public interface CommonDao {

    boolean isResourceExist(String rid);
    
    boolean isOwner(String userId, String rid);

    boolean isMemberOrOwner(String userId, String postId);

    void delete(String rid);

}
