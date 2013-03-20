package com.tissue.security.dao;

import com.tissue.security.UserDetailsImpl;

public interface UserDetailsDao {

    UserDetailsImpl getUserByUsername(String username);

}
