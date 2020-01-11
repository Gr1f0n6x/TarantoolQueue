package org.tarantool.queue.internals.builders;

public interface LimitBuilder<T> extends TtlBuilder<T> {
    LimitBuilder<T> setTimeout(long timeout);
}
