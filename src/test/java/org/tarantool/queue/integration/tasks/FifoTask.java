package org.tarantool.queue.integration.tasks;

import org.tarantool.queue.QueueType;
import org.tarantool.queue.annotations.Queue;

import java.util.Objects;

@Queue(name = "fifo_queue", type = QueueType.FIFO)
public class FifoTask {
    private String value;

    public FifoTask() {
    }

    public FifoTask(String value) {
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
        FifoTask fifoTask = (FifoTask) o;
        return Objects.equals(value, fifoTask.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "FifoTask{" +
                "value='" + value + '\'' +
                '}';
    }
}
