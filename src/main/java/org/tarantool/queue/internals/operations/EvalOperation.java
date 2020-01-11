package org.tarantool.queue.internals.operations;

import org.tarantool.TarantoolClient;
import org.tarantool.queue.TaskInfo;
import org.tarantool.queue.internals.Meta;

import java.util.List;
import java.util.concurrent.CompletionStage;

public class EvalOperation<T> implements Operation<T> {
    private final TarantoolClient client;
    private final Meta<T> meta;
    private final String operation;

    public EvalOperation(TarantoolClient client, Meta<T> meta, String operation) {
        this.client = client;
        this.meta = meta;
        this.operation = operation;
    }

    @Override
    public TaskInfo<T> runSync() {
        List<?> result = client.syncOps().eval(operation);
        return meta.resultToTaskInfo(result);
    }

    @Override
    public CompletionStage<TaskInfo<T>> runAsync() {
        return client.composableAsyncOps().eval(operation)
                .thenApply(meta::resultToTaskInfo);
    }
}
