package org.tarantool.queue.auto.data;

import com.google.common.base.Joiner;

public class ManagerFactoryOutput extends Output {
    public static String output = Joiner.on(NEW_LINE).join(
            "package org.tarantool.queue.generated;",

            "import com.fasterxml.jackson.databind.ObjectMapper;",
            "import com.fasterxml.jackson.databind.ObjectReader;",
            "import com.fasterxml.jackson.databind.ObjectWriter;",
            "import org.tarantool.TarantoolClient;",
            "import test.Task;",

            "public final class TaskManagerFactory {",
            "private final TarantoolClient tarantoolClient;",

            "private final ObjectMapper objectMapper;",

            "public TaskManagerFactory(final TarantoolClient tarantoolClient) {",
            "this.tarantoolClient = tarantoolClient;",
            "this.objectMapper = new ObjectMapper();",
            "}",

            "public TaskManagerFactory(final TarantoolClient tarantoolClient,",
            "final ObjectMapper objectMapper) {",
            "this.tarantoolClient = tarantoolClient;",
            "this.objectMapper = objectMapper;",
            "}",

            "public TaskQueue taskQueueManager() {",
            "ObjectReader reader = objectMapper.readerFor(Task.class);",
            "ObjectWriter writer = objectMapper.writerFor(Task.class);",
            "return new TaskQueue(tarantoolClient, reader, writer);",
            "}",
            "}"
    );
}
