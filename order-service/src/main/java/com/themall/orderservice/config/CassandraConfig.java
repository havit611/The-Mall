//package com.themall.orderservice.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
//import org.springframework.data.cassandra.config.SchemaAction;
//import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
//import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
//
//import java.util.Collections;
//import java.util.List;
//
//@Configuration
//public class CassandraConfig extends AbstractCassandraConfiguration {
//
//    @Override
//    protected String getKeyspaceName() {
//        return "order_service";
//    }
//
//    @Override
//    protected String getContactPoints() {
//        return "localhost";
//    }
//
//    @Override
//    protected int getPort() {
//        return 9042;
//    }
//
//    @Override
//    protected String getLocalDataCenter() {
//        return "datacenter1";
//    }
//
//    @Override
//    public SchemaAction getSchemaAction() {
//        return SchemaAction.CREATE_IF_NOT_EXISTS;
//    }
//
//    @Override
//    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
//        return Collections.singletonList(
//                CreateKeyspaceSpecification.createKeyspace("order_service")
//                        .ifNotExists()
//                        .with(KeyspaceOption.DURABLE_WRITES, true)
//                        .withSimpleReplication(1)
//        );
//    }
//}
package com.themall.orderservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;

import java.util.Collections;
import java.util.List;

@Configuration
public class CassandraConfig extends AbstractCassandraConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CassandraConfig.class);

    @PostConstruct
    public void logConfig() {
        log.info("=== Cassandra Configuration ===");
        log.info("Keyspace: {}", getKeyspaceName());
        log.info("Contact Points: {}", getContactPoints());
        log.info("Port: {}", getPort());
        log.info("Data Center: {}", getLocalDataCenter());
        log.info("Schema Action: {}", getSchemaAction());
        log.info("================================");
    }

    @Override
    protected String getKeyspaceName() {
        return "order_service";
    }

    @Override
    protected String getContactPoints() {
        return "localhost";
    }

    @Override
    protected int getPort() {
        return 9042;
    }

    @Override
    protected String getLocalDataCenter() {
        return "datacenter1";
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        return Collections.singletonList(
                CreateKeyspaceSpecification.createKeyspace("order_service")
                        .ifNotExists()
                        .with(KeyspaceOption.DURABLE_WRITES, true)
                        .withSimpleReplication(1)
        );
    }
}