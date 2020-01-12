package org.tarantool.queue;

import java.util.Objects;

public final class TaskInfo<T> {
    public final long id;
    public final TaskStatus taskStatus;
    public final T task;

    public TaskInfo(long id, TaskStatus taskStatus, T task) {
        this.id = id;
        this.taskStatus = taskStatus;
        this.task = task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskInfo<?> taskInfo = (TaskInfo<?>) o;
        return id == taskInfo.id &&
                taskStatus == taskInfo.taskStatus &&
                Objects.equals(task, taskInfo.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskStatus, task);
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "id=" + id +
                ", taskStatus=" + taskStatus +
                ", task=" + task +
                '}';
    }
}
