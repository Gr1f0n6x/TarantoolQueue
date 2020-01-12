package org.tarantool.queue.auto.data;

import com.google.common.base.Joiner;

public class UtubeTtlOutput extends Output {
    public static String output = Joiner.on(NEW_LINE).join(
            "            package org.tarantool.queue.generated;",

            "import com.fasterxml.jackson.databind.ObjectReader;",
            "import com.fasterxml.jackson.databind.ObjectWriter;",
            "import java.lang.Exception;",
            "import java.lang.IllegalArgumentException;",
            "import java.lang.Number;",
            "import java.lang.RuntimeException;",
            "import java.lang.String;",
            "import java.lang.StringBuilder;",
            "import java.util.List;",
            "import org.tarantool.TarantoolClient;",
            "import org.tarantool.queue.QueueManager;",
            "import org.tarantool.queue.TaskInfo;",
            "import org.tarantool.queue.TaskStatus;",
            "import org.tarantool.queue.internals.Meta;",
            "import org.tarantool.queue.internals.builders.UtubeTtlBuilder;",
            "import org.tarantool.queue.internals.operations.EvalOperation;",
            "import org.tarantool.queue.internals.operations.Operation;",
            "import test.Task;",

            "    public final class TaskQueue implements QueueManager<Task> {",
            "        private final String queueName = \"queue\";",

            "        private final TarantoolClient tarantoolClient;",

            "        private final ObjectReader reader;",

            "        private final ObjectWriter writer;",

            "        private final Meta<Task> meta;",

            "        public TaskQueue(final TarantoolClient tarantoolClient, final ObjectReader reader,",
            "                         final ObjectWriter writer) {",
            "            this.tarantoolClient = tarantoolClient;",
            "            this.reader = reader;",
            "            this.writer = writer;",
            "            this.meta = new TaskMeta(reader, writer);",
            "        }",

            "        public Operation<Task> put(final Task task) {",
            "            try {",
            "                String taskJson = writer.writeValueAsString(task);",
            "                return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:put('%s')\", queueName, taskJson));",
            "            } catch (Exception e) {",
            "                throw new RuntimeException(e);",
            "            }",
            "        }",

            "        public Operation<Task> release(final long taskId) {",
            "            return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:release(%s)\", queueName, taskId));",
            "        }",

            "        public Operation<Task> ack(final long taskId) {",
            "            return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:ack(%s)\", queueName, taskId));",
            "        }",

            "        public Operation<Task> peek(final long taskId) {",
            "            return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:peek(%s)\", queueName, taskId));",
            "        }",

            "        public Operation<Task> bury(final long taskId) {",
            "            return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:bury(%s)\", queueName, taskId));",
            "        }",

            "        public Operation<Task> take() {",
            "            return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:take(0)\", queueName));",
            "        }",

            "        public Operation<Task> takeWithTimeout(final long timeout) {",
            "            return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:take(%s)\", queueName, timeout));",
            "        }",

            "        public Operation<Task> delete(final long taskId) {",
            "            return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:delete(%s)\", queueName, taskId));",
            "        }",

            "        public Operation<Task> touch(final long taskId, final long increment) {",
            "            if (increment < 0) {",
            "                throw new IllegalArgumentException(\"increment should be >= 0\");",
            "            }",
            "            return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:touch(%s, %s)\", queueName, taskId, increment));",
            "        }",

            "        public UtubeTtlBuilder<Task> putWithOptions(final Task task) {",
            "            try {",
            "                String taskJson = writer.writeValueAsString(task);",
            "                return new PutOperationBuilder(tarantoolClient, meta, taskJson);",
            "            } catch (Exception e) {",
            "                throw new RuntimeException(e);",
            "            }",
            "        }",

            "        public UtubeTtlBuilder<Task> releaseWithOptions(final long taskId) {",
            "            return new ReleaseOperationBuilder(tarantoolClient, meta, taskId);",
            "        }",

            "        private class TaskMeta extends Meta<Task> {",
            "            private final ObjectReader reader;",

            "            private final ObjectWriter writer;",

            "            public TaskMeta(final ObjectReader reader, final ObjectWriter writer) {",
            "                this.reader = reader;",
            "                this.writer = writer;",
            "            }",

            "            public TaskInfo<Task> fromList(final List<?> values) {",
            "                try {",
            "                    long id = ((Number) values.get(0)).longValue();",
            "                    TaskStatus taskStatus = TaskStatus.getBySymbol((String) values.get(1));",
            "                    Task task = reader.readValue((String) values.get(2));",
            "                    return new TaskInfo<>(id, taskStatus, task);",
            "                } catch(Exception e) {",
            "                    throw new RuntimeException(e);",
            "                }",
            "            }",
            "        }",

            "        public final class PutOperationBuilder implements UtubeTtlBuilder<Task> {",
            "            private long ttl;",

            "            private long ttr;",

            "            private long priority;",

            "            private long delay;",

            "            private String utube;",

            "            private final String taskJson;",

            "            private final TarantoolClient tarantoolClient;",

            "            private final Meta<Task> meta;",

            "            public PutOperationBuilder(final TarantoolClient tarantoolClient, final Meta<Task> meta,",
            "                                       String taskJson) {",
            "                this.taskJson = taskJson;",
            "                this.tarantoolClient = tarantoolClient;",
            "                this.meta = meta;",
            "            }",

            "            public UtubeTtlBuilder<Task> setTtl(final long ttl) {",
            "                if (ttl < 0) {",
            "                    throw new RuntimeException(\"ttl must be >= 0\");",
            "                }",
            "                this.ttl = ttl;",
            "                return this;",
            "            }",

            "            public UtubeTtlBuilder<Task> setTtr(final long ttr) {",
            "                if (ttr < 0) {",
            "                    throw new RuntimeException(\"ttr must be >= 0\");",
            "                }",
            "                this.ttr = ttr;",
            "                return this;",
            "            }",

            "            public UtubeTtlBuilder<Task> setUtube(final String utube) {",
            "                if (utube == null || utube.isEmpty()) {",
            "                    throw new RuntimeException(\"utube name must not be null or empty\");",
            "                }",
            "                this.utube = utube;",
            "                return this;",
            "            }",

            "            public UtubeTtlBuilder<Task> setPriority(final long priority) {",
            "                if (priority < 0) {",
            "                    throw new RuntimeException(\"priority must be >= 0\");",
            "                }",
            "                this.priority = priority;",
            "                return this;",
            "            }",

            "            public UtubeTtlBuilder<Task> setDelay(final long delay) {",
            "                if (delay < 0) {",
            "                    throw new RuntimeException(\"delay must be >= 0\");",
            "                }",
            "                this.delay = delay;",
            "                return this;",
            "            }",

            "            public Operation<Task> build() {",
            "                StringBuilder options = new StringBuilder();",
            "                if (ttl > 0) {",
            "                    options.append(\"'ttl'=\").append(ttl).append(\",\");",
            "                }",
            "                if (ttr > 0) {",
            "                    options.append(\"'ttr'=\").append(ttr).append(\",\");",
            "                }",
            "                if (delay > 0) {",
            "                    options.append(\"'delay'=\").append(delay).append(\",\");",
            "                }",
            "                if (priority > 0) {",
            "                    options.append(\"'pri'=\").append(priority).append(\",\");",
            "                }",
            "                if (utube != null) {",
            "                    options.append(\"'ttl'=\").append(\"'\").append(ttl).append(\"'\").append(\",\");",
            "                }",
            "                return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:put('%s', {%s})\", queueName, taskJson, options.toString()));",
            "            }",
            "        }",

            "        public final class ReleaseOperationBuilder implements UtubeTtlBuilder<Task> {",
            "            private long ttl;",

            "            private long ttr;",

            "            private long priority;",

            "            private long delay;",

            "            private String utube;",

            "            private final long taskId;",

            "            private final TarantoolClient tarantoolClient;",

            "            private final Meta<Task> meta;",

            "            public ReleaseOperationBuilder(final TarantoolClient tarantoolClient, final Meta<Task> meta,",
            "                                           long taskId) {",
            "                this.tarantoolClient = tarantoolClient;",
            "                this.meta = meta;",
            "                this.taskId = taskId;",
            "            }",

            "            public UtubeTtlBuilder<Task> setTtl(final long ttl) {",
            "                if (ttl < 0) {",
            "                    throw new RuntimeException(\"ttl must be >= 0\");",
            "                }",
            "                this.ttl = ttl;",
            "                return this;",
            "            }",

            "            public UtubeTtlBuilder<Task> setTtr(final long ttr) {",
            "                if (ttr < 0) {",
            "                    throw new RuntimeException(\"ttr must be >= 0\");",
            "                }",
            "                this.ttr = ttr;",
            "                return this;",
            "            }",

            "            public UtubeTtlBuilder<Task> setUtube(final String utube) {",
            "                if (utube == null || utube.isEmpty()) {",
            "                    throw new RuntimeException(\"utube name must not be null or empty\");",
            "                }",
            "                this.utube = utube;",
            "                return this;",
            "            }",

            "            public UtubeTtlBuilder<Task> setPriority(final long priority) {",
            "                if (priority < 0) {",
            "                    throw new RuntimeException(\"priority must be >= 0\");",
            "                }",
            "                this.priority = priority;",
            "                return this;",
            "            }",

            "            public UtubeTtlBuilder<Task> setDelay(final long delay) {",
            "                if (delay < 0) {",
            "                    throw new RuntimeException(\"delay must be >= 0\");",
            "                }",
            "                this.delay = delay;",
            "                return this;",
            "            }",

            "            public Operation<Task> build() {",
            "                StringBuilder options = new StringBuilder();",
            "                if (ttl > 0) {",
            "                    options.append(\"'ttl'=\").append(ttl).append(\",\");",
            "                }",
            "                if (ttr > 0) {",
            "                    options.append(\"'ttr'=\").append(ttr).append(\",\");",
            "                }",
            "                if (delay > 0) {",
            "                    options.append(\"'delay'=\").append(delay).append(\",\");",
            "                }",
            "                if (priority > 0) {",
            "                    options.append(\"'pri'=\").append(priority).append(\",\");",
            "                }",
            "                if (utube != null) {",
            "                    options.append(\"'ttl'=\").append(\"'\").append(ttl).append(\"'\").append(\",\");",
            "                }",
            "                return new EvalOperation<>(tarantoolClient, meta, String.format(\"return queue.tube.%s:release(%s, {%s})\", queueName, taskId, options.toString()));",
            "            }",
            "        }",
            "    }");
}
