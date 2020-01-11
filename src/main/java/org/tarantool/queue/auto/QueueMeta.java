package org.tarantool.queue.auto;

import com.squareup.javapoet.TypeName;
import org.tarantool.queue.QueueType;
import org.tarantool.queue.annotations.Queue;

import javax.lang.model.element.TypeElement;

final class QueueMeta {
    public final QueueType queueType;
    public final String queueName;
    public final String taskManagerName;
    public final TypeName classType;
    public final String className;

    public static QueueMeta getInstance(TypeElement element) {
        Queue queue = element.getAnnotation(Queue.class);
        validateQueueName(queue.name());

        String taskManagerName = element.getSimpleName().toString() + "Queue";
        return new QueueMeta(
                queue.type(),
                queue.name(),
                taskManagerName,
                TypeName.get(element.asType()),
                element.getSimpleName().toString()
        );
    }

    private static void validateQueueName(String queueName) {
        if (queueName.isEmpty()) {
            throw new IllegalArgumentException("Queue name should not be empty");
        }

        if (queueName.length() > 32) {
            throw new IllegalArgumentException("Queue name should be up to 32 characters");
        }
    }

    private QueueMeta(QueueType queueType, String queueName, String taskManagerName, TypeName classType, String className) {
        this.queueType = queueType;
        this.queueName = queueName;
        this.taskManagerName = taskManagerName;
        this.classType = classType;
        this.className = className;
    }
}
