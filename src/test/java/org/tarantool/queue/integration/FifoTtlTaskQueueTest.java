package org.tarantool.queue.integration;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tarantool.queue.TaskInfo;
import org.tarantool.queue.TaskStatus;
import org.tarantool.queue.generated.FifoTtlTaskQueue;
import org.tarantool.queue.integration.tasks.FifoTtlTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class FifoTtlTaskQueueTest extends IntegrationTest {
    public static FifoTtlTaskQueue queue;

    @BeforeClass
    public static void setUpQueue() {
        client.syncOps().eval("queue.create_tube('fifo_ttl_queue', 'fifottl', {if_not_exists=true})");
        queue = managerFactory.fifoTtlTaskQueueManager();
    }

    @AfterClass
    public static void dropQueue() {
        client.syncOps().eval("queue.tube.fifo_ttl_queue:drop()");
    }

    @After
    public void truncate() {
        client.syncOps().eval("queue.tube.fifo_ttl_queue:truncate()");
    }

    @Test
    public void putWithOptions() {
        FifoTtlTask ttlTask = new FifoTtlTask("value");
        TaskInfo<FifoTtlTask> result = queue
                .putWithOptions(ttlTask)
                .setTtl(1)
                .setTtr(1)
                .setDelay(1)
                .setPriority(0)
                .build()
                .runSync();

        assertEquals(result, new TaskInfo<>(0, TaskStatus.DELAYED, ttlTask));
    }

    @Test
    public void putWithOptionsAsync() throws InterruptedException, ExecutionException, TimeoutException {
        FifoTtlTask ttlTask = new FifoTtlTask("value");
        TaskInfo<FifoTtlTask> result = queue
                .putWithOptions(ttlTask)
                .setTtl(1)
                .setTtr(1)
                .setDelay(1)
                .setPriority(0)
                .build()
                .runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);

        assertEquals(result, new TaskInfo<>(0, TaskStatus.DELAYED, ttlTask));
    }

    @Test
    public void releaseWithOptions() {
        FifoTtlTask ttlTask = new FifoTtlTask("value");
        queue.put(ttlTask).runSync();
        TaskInfo<FifoTtlTask> taken = queue.take().runSync();
        TaskInfo<FifoTtlTask> result = queue
                .releaseWithOptions(0)
                .setTtl(1)
                .setTtr(1)
                .setPriority(0)
                .build()
                .runSync();

        assertEquals(taken, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
        assertEquals(result, new TaskInfo<>(0, TaskStatus.READY, ttlTask));
    }

    @Test
    public void releaseWithOptionsAsync() throws InterruptedException, ExecutionException, TimeoutException {
        FifoTtlTask ttlTask = new FifoTtlTask("value");
        queue.put(ttlTask).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<FifoTtlTask> taken = queue.take().runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<FifoTtlTask> result = queue
                .releaseWithOptions(0)
                .setTtl(1)
                .setTtr(1)
                .setPriority(0)
                .build()
                .runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);

        assertEquals(taken, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
        assertEquals(result, new TaskInfo<>(0, TaskStatus.READY, ttlTask));
    }

    @Test
    public void touch() {
        FifoTtlTask ttlTask = new FifoTtlTask("value");
        queue.put(ttlTask).runSync();
        TaskInfo<FifoTtlTask> taken = queue.take().runSync();
        TaskInfo<FifoTtlTask> result = queue.touch(0, 5).runSync();
        queue.ack(0).runSync();
        assertEquals(taken, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
        assertEquals(result, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
    }

    @Test
    public void touchAsync() throws InterruptedException, ExecutionException, TimeoutException {
        FifoTtlTask ttlTask = new FifoTtlTask("value");
        queue.put(ttlTask).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<FifoTtlTask> taken = queue.take().runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<FifoTtlTask> result = queue.touch(0, 5).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        queue.ack(0).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        assertEquals(taken, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
        assertEquals(result, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
    }
}
