package org.tarantool.queue.integration;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.tarantool.TarantoolClient;
import org.tarantool.TarantoolClientConfig;
import org.tarantool.TarantoolClientImpl;
import org.tarantool.queue.generated.TaskManagerFactory;
import org.testcontainers.containers.GenericContainer;

public abstract class IntegrationTest {
    @ClassRule
    public static GenericContainer tarantool = new GenericContainer<>("tarantool/tarantool:2.3")
            .withExposedPorts(3301);

    protected static TarantoolClient client;
    protected static TaskManagerFactory managerFactory;

    @BeforeClass
    public static void setUp() {
        TarantoolClientConfig config = new TarantoolClientConfig();
        config.username = "guest";
        // 5 seconds
        config.operationExpiryTimeMillis = 5000;

        String host = tarantool.getContainerIpAddress();
        int port = tarantool.getMappedPort(3301);

        client = new TarantoolClientImpl(String.format("%s:%s", host, port), config);

        client.syncOps().eval("queue = require('queue')");

        managerFactory = new TaskManagerFactory(client);
    }

    @AfterClass
    public static void cleanUp() {
        client.close();
    }
}
