package org.tarantool.queue.internals.builders;

import org.tarantool.queue.internals.operations.Operation;

public interface Builder<T> {
    Operation<T> build();
}
