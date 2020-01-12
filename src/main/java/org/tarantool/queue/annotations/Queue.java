package org.tarantool.queue.annotations;

import org.tarantool.queue.QueueType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Queue {
    String name();
    QueueType type();
}
