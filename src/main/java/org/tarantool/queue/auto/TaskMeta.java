package org.tarantool.queue.auto;

import org.tarantool.queue.QueueType;
import org.tarantool.queue.annotations.Queue;

import javax.lang.model.element.TypeElement;

final class TaskMeta {
    public final QueueType queueType;
    public final String queueName;
    public final String taskManagerName;

    public static TaskMeta getInstance(TypeElement element) {
        Queue queue = element.getAnnotation(Queue.class);
        validateQueueName(queue.name());

        String taskManagerName = element.getSimpleName().toString() + "Queue";
        return new TaskMeta(queue.type(), queue.name(), taskManagerName);
    }

    private static void validateQueueName(String queueName) {
        if (queueName.isEmpty()) {
            throw new IllegalArgumentException("Queue name should not be empty");
        }

        if (queueName.length() > 32) {
            throw new IllegalArgumentException("Queue name should be up to 32 characters");
        }
    }

    private TaskMeta(QueueType queueType, String queueName, String taskManagerName) {
        this.queueType = queueType;
        this.queueName = queueName;
        this.taskManagerName = taskManagerName;
    }
}
