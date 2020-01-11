package org.tarantool.queue;

public final class TaskInfo<T> {
    public final long id;
    public final TaskStatus taskStatus;
    public final T task;

    public TaskInfo(long id, TaskStatus taskStatus, T task) {
        this.id = id;
        this.taskStatus = taskStatus;
        this.task = task;
    }
}
