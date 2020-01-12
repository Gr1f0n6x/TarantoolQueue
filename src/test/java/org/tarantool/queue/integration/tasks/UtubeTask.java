package org.tarantool.queue.integration.tasks;

import org.tarantool.queue.QueueType;
import org.tarantool.queue.annotations.Queue;

import java.util.Objects;

@Queue(name = "utube_queue", type = QueueType.UTUBE)
public class UtubeTask {
    private String value;

    public UtubeTask() {
    }

    public UtubeTask(String value) {
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
        UtubeTask utubeTask = (UtubeTask) o;
        return Objects.equals(value, utubeTask.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "UtubeTask{" +
                "value='" + value + '\'' +
                '}';
    }
}
