package org.tarantool.queue.internals.builders;

public interface LimitBuilder<T> extends TtlBuilder<T>, UtubeBuilder<T> {
    LimitBuilder<T> setTimeout(long timeout);
}
