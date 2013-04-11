package com.tissue.core.util;

import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;

public class OrientDataSource {
    private OGraphDatabasePool pool;

    public OrientDataSource(String dburl, String user, String pass, Integer minium, Integer max) {
        pool = new OGraphDatabasePool(dburl.trim(), user.trim(), pass.trim());
        pool.setup(minium, max);
    }

    public OGraphDatabase getDB() {
        return pool.acquire();
    }

    public void destroy() {
        pool.close();
    }
}
