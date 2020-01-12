package org.tarantool.queue.auto;

import javax.annotation.processing.Filer;

final class FifoQueueManagerGenerator extends QueueManagerGenerator {
    public FifoQueueManagerGenerator(Filer filer, QueueMeta queueMeta) {
        super(filer, queueMeta);
    }
}