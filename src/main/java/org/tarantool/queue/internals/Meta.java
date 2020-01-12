package org.tarantool.queue.internals;

import org.tarantool.queue.TaskInfo;

import java.util.List;

public abstract class Meta<T> {
    public abstract TaskInfo<T> fromList(List<?> values);

    // values -> List of List<?>
    public final TaskInfo<T> resultToTaskInfo(List<?> values) {
        if (values.size() == 1) {
            return fromList(((List<?>) values.get(0)));
        } else {
            return null;
        }
    }
}
