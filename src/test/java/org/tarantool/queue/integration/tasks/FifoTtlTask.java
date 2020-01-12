package org.tarantool.queue.integration.tasks;

import org.tarantool.queue.QueueType;
import org.tarantool.queue.annotations.Queue;

import java.util.Objects;

@Queue(name = "fifo_ttl_queue", type = QueueType.FIFO_TTL)
public class FifoTtlTask {
    private String value;

    public FifoTtlTask() {
    }

    public FifoTtlTask(String value) {
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
        FifoTtlTask that = (FifoTtlTask) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "FifoTtlTask{" +
                "value='" + value + '\'' +
                '}';
    }
}
