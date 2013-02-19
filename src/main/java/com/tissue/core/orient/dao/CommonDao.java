package com.tissue.core.orient.dao;

public interface CommonDao {

    boolean isResourceExist(String rid);
    
    boolean isOwner(String userId, String rid);

    /**
     * Check if the user is a member or not of the plan
     * that the post belongs.
     * @param userId user id
     * @param postId post id for which the plan's membership need to check
     */
    boolean isMemberOrOwner(String userId, String postId);

    void delete(String rid);

}
