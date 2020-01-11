package org.tarantool.queue.internals.builders;

public interface TtlBuilder<T> extends Builder<T> {
    TtlBuilder<T> setPriority(long priority);

    TtlBuilder<T> setTtl(long ttl);

    TtlBuilder<T> setTtr(long ttr);

    TtlBuilder<T> setDelay(long delay);
}
