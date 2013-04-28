package com.tissue.core.datasources;

import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component("dataSource")
public class OrientDataSource {

    //private OGraphDatabasePool pool;

    @Value("${orientdb_url}")
    private String orientdb_url;

    @Value("${orientdb_username}")
    private String db_username;

    @Value("${orientdb_password}")
    private String db_password;

    @Value("${orientdb_min}")
    private Integer db_min;

    @Value("${orientdb_max}")
    private Integer db_max;

    public OrientGraph getDB() {
        return new OrientGraph(orientdb_url.trim(), db_username.trim(), db_password.trim());
    }

    /**
    public OGraphDatabase getDB() {
        if(pool == null) {
            pool = new OGraphDatabasePool(orientdb_url.trim(), db_username.trim(), db_password.trim());
            pool.setup(db_min, db_max);
        }
        return pool.acquire();
    }
    */

    public void destroy() {
        System.out.println("++++++++++ destroy in data souce");
        //pool.close();
    }
}
