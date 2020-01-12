package org.tarantool.queue.auto.data;

import com.google.common.base.Joiner;

public class FifoOutput extends Output {
    public static String output = Joiner.on(NEW_LINE).join(

            "package org.tarantool.queue.generated;",

            "import com.fasterxml.jackson.databind.ObjectReader;",
            "import com.fasterxml.jackson.databind.ObjectWriter;",
            "import java.lang.Exception;",
            "import java.lang.Number;",
            "import java.lang.RuntimeException;",
            "import java.lang.String;",
            "import java.util.List;",
            "import org.tarantool.TarantoolClient;",
            "import org.tarantool.queue.QueueManager;",
            "import org.tarantool.queue.TaskInfo;",
            "import org.tarantool.queue.TaskStatus;",
            "import org.tarantool.queue.internals.Meta;",
            "import org.tarantool.queue.internals.operations.EvalOperation;",
            "import org.tarantool.queue.internals.operations.Operation;",
            "import test.Task;",

            "public final class TaskQueue implements QueueManager<Task> {",
            "private final String queueName = \"queue\";",

            "private final TarantoolClient tarantoolClient;",

            "private final ObjectReader reader;",

            "private final ObjectWriter writer;",

            "private final Meta<Task> meta;",

            "public TaskQueue(final TarantoolClient tarantoolClient, final ObjectReader reader,",
            "final ObjectWriter writer) {",
            "this.tarantoolClient = tarantoolClient;",
            "this.reader = reader;",
            "this.writer = writer;",
            "this.meta = new TaskMeta(reader, writer);",
            "}",

            "public Operation<Task> put(final Task task) {",
            "try {",
            "String taskJson = writer.writeValueAsString(task);",
            "return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:put('%s')\", queueName, taskJson));",
            "} catch (Exception e) {",
            "throw new RuntimeException(e);",
            "}",
            "}",

            "public Operation<Task> release(final long taskId) {",
            "return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:release(%s)\", queueName, taskId));",
            "}",

            "public Operation<Task> ack(final long taskId) {",
            "return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:ack(%s)\", queueName, taskId));",
            "}",

            "public Operation<Task> peek(final long taskId) {",
            "return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:peek(%s)\", queueName, taskId));",
            "}",

            "public Operation<Task> bury(final long taskId) {",
            "return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:bury(%s)\", queueName, taskId));",
            "}",

            "public Operation<Task> take() {",
            "return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:take(0)\", queueName));",
            "}",

            "public Operation<Task> takeWithTimeout(final long timeout) {",
            "return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:take(%s)\", queueName, timeout));",
            "}",

            "public Operation<Task> delete(final long taskId) {",
            "return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:delete(%s)\", queueName, taskId));",
            "}",

            "private class TaskMeta extends Meta<Task> {",
            "private final ObjectReader reader;",

            "private final ObjectWriter writer;",

            "public TaskMeta(final ObjectReader reader, final ObjectWriter writer) {",
            "this.reader = reader;",
            "this.writer = writer;",
            "}",

            "public TaskInfo<Task> fromList(final List<?> values) {",
            "try {",
            "long id = ((Number) values.get(0)).longValue();",
            "TaskStatus taskStatus = TaskStatus.getBySymbol((String) values.get(1));",
            "Task task = reader.readValue((String) values.get(2));",
            "return new TaskInfo<>(id, taskStatus, task);",
            "} catch(Exception e) {",
            "throw new RuntimeException(e);",
            "}",
            "}",
            "}",
            "}"
    );
}
