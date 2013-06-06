package com.tissue.core.datasources;

import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component("dataSource")
public class OrientDataSource {

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

    public void destroy() {
    }
}
