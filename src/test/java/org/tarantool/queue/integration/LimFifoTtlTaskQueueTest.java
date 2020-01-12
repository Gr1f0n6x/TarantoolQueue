package org.tarantool.queue.integration;

import org.junit.*;
import org.tarantool.queue.TaskInfo;
import org.tarantool.queue.TaskStatus;
import org.tarantool.queue.generated.LimFifoTtlTaskQueue;
import org.tarantool.queue.integration.tasks.LimFifoTtlTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

@Ignore("Ignore tests for limfifottl due to docker container for tarantool has queue module without limfifottl support (1.0.2)")
public class LimFifoTtlTaskQueueTest extends IntegrationTest {
    public static LimFifoTtlTaskQueue queue;

    @BeforeClass
    public static void setUpQueue() {
        client.syncOps().eval("queue.create_tube('lim_fifo_ttl_queue', 'limfifottl', {if_not_exists=true, capacity=10})");
        queue = managerFactory.limFifoTtlTaskQueueManager();
    }

    @AfterClass
    public static void dropQueue() {
        client.syncOps().eval("queue.tube.lim_fifo_ttl_queue:drop()");
    }

    @After
    public void truncate() {
        client.syncOps().eval("queue.tube.lim_fifo_ttl_queue:truncate()");
    }

    @Test
    public void putWithOptions() {
        LimFifoTtlTask ttlTask = new LimFifoTtlTask("value");
        TaskInfo<LimFifoTtlTask> result = queue
                .putWithOptions(ttlTask)
                .setTtl(1)
                .setTtr(1)
                .setDelay(1)
                .setPriority(0)
                .setTimeout(1)
                .build()
                .runSync();

        assertEquals(result, new TaskInfo<>(0, TaskStatus.DELAYED, ttlTask));
    }

    @Test
    public void putWithOptionsAsync() throws InterruptedException, ExecutionException, TimeoutException {
        LimFifoTtlTask ttlTask = new LimFifoTtlTask("value");
        TaskInfo<LimFifoTtlTask> result = queue
                .putWithOptions(ttlTask)
                .setTtl(1)
                .setTtr(1)
                .setDelay(1)
                .setPriority(0)
                .setTimeout(1)
                .build()
                .runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);

        assertEquals(result, new TaskInfo<>(0, TaskStatus.DELAYED, ttlTask));
    }

    @Test
    public void releaseWithOptions() {
        LimFifoTtlTask ttlTask = new LimFifoTtlTask("value");
        queue.put(ttlTask).runSync();
        TaskInfo<LimFifoTtlTask> taken = queue.take().runSync();
        TaskInfo<LimFifoTtlTask> result = queue
                .releaseWithOptions(0)
                .setTtl(1)
                .setTtr(1)
                .setPriority(0)
                .setTimeout(1)
                .build()
                .runSync();

        assertEquals(taken, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
        assertEquals(result, new TaskInfo<>(0, TaskStatus.READY, ttlTask));
    }

    @Test
    public void releaseWithOptionsAsync() throws InterruptedException, ExecutionException, TimeoutException {
        LimFifoTtlTask ttlTask = new LimFifoTtlTask("value");
        queue.put(ttlTask).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<LimFifoTtlTask> taken = queue.take().runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<LimFifoTtlTask> result = queue
                .releaseWithOptions(0)
                .setTtl(1)
                .setTtr(1)
                .setPriority(0)
                .setTimeout(1)
                .build()
                .runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);

        assertEquals(taken, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
        assertEquals(result, new TaskInfo<>(0, TaskStatus.READY, ttlTask));
    }

    @Test
    public void touch() {
        LimFifoTtlTask ttlTask = new LimFifoTtlTask("value");
        queue.put(ttlTask).runSync();
        TaskInfo<LimFifoTtlTask> taken = queue.take().runSync();
        TaskInfo<LimFifoTtlTask> result = queue.touch(0, 5).runSync();
        queue.ack(0).runSync();
        assertEquals(taken, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
        assertEquals(result, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
    }

    @Test
    public void touchAsync() throws InterruptedException, ExecutionException, TimeoutException {
        LimFifoTtlTask ttlTask = new LimFifoTtlTask("value");
        queue.put(ttlTask).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<LimFifoTtlTask> taken = queue.take().runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<LimFifoTtlTask> result = queue.touch(0, 5).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        queue.ack(0).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        assertEquals(taken, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
        assertEquals(result, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
    }
}
