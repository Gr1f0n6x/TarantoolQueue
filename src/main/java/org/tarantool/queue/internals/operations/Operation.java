package org.tarantool.queue.internals.operations;

import org.tarantool.queue.TaskInfo;

import java.util.concurrent.CompletionStage;

public interface Operation<T> {
    TaskInfo<T> runSync();

    CompletionStage<TaskInfo<T>> runAsync();
}
