package com.tissue.core.security.dao;

import com.tissue.core.security.UserDetailsImpl;

public interface UserDetailsDao {

    UserDetailsImpl getUserByUsername(String username);

}
