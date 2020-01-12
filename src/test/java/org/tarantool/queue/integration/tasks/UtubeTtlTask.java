package org.tarantool.queue.integration.tasks;

import org.tarantool.queue.QueueType;
import org.tarantool.queue.annotations.Queue;

import java.util.Objects;

@Queue(name = "utube_ttl_queue", type = QueueType.UTUBE_TTL)
public class UtubeTtlTask {
    private String value;

    public UtubeTtlTask() {
    }

    public UtubeTtlTask(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UtubeTtlTask utubeTtlTask = (UtubeTtlTask) o;
        return Objects.equals(value, utubeTtlTask.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "UtubeTtlTask{" +
                "value='" + value + '\'' +
                '}';
    }
}
