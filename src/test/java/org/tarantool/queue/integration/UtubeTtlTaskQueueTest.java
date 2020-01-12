package org.tarantool.queue.integration;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tarantool.queue.TaskInfo;
import org.tarantool.queue.TaskStatus;
import org.tarantool.queue.generated.UtubeTtlTaskQueue;
import org.tarantool.queue.integration.tasks.UtubeTtlTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class UtubeTtlTaskQueueTest extends IntegrationTest {
    public static UtubeTtlTaskQueue queue;

    @BeforeClass
    public static void setUpQueue() {
        client.syncOps().eval("queue.create_tube('utube_ttl_queue', 'utubettl', {if_not_exists=true})");
        queue = managerFactory.utubeTtlTaskQueueManager();
    }

    @AfterClass
    public static void dropQueue() {
        client.syncOps().eval("queue.tube.utube_ttl_queue:drop()");
    }

    @After
    public void truncate() {
        client.syncOps().eval("queue.tube.utube_ttl_queue:truncate()");
    }

    @Test
    public void putWithOptions() {
        UtubeTtlTask ttlTask = new UtubeTtlTask("value");
        TaskInfo<UtubeTtlTask> result = queue
                .putWithOptions(ttlTask)
                .setUtube("my_utube")
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
        UtubeTtlTask ttlTask = new UtubeTtlTask("value");
        TaskInfo<UtubeTtlTask> result = queue
                .putWithOptions(ttlTask)
                .setUtube("my_utube")
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
        UtubeTtlTask ttlTask = new UtubeTtlTask("value");
        queue.put(ttlTask).runSync();
        TaskInfo<UtubeTtlTask> taken = queue.take().runSync();
        TaskInfo<UtubeTtlTask> result = queue
                .releaseWithOptions(0)
                .setUtube("my_utube")
                .setTtl(1)
                .setTtr(1)
                .setDelay(0)
                .setPriority(0)
                .build()
                .runSync();

        assertEquals(taken, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
        assertEquals(result, new TaskInfo<>(0, TaskStatus.READY, ttlTask));
    }

    @Test
    public void releaseWithOptionsAsync() throws InterruptedException, ExecutionException, TimeoutException {
        UtubeTtlTask ttlTask = new UtubeTtlTask("value");
        queue.put(ttlTask).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<UtubeTtlTask> taken = queue.take().runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<UtubeTtlTask> result = queue
                .releaseWithOptions(0)
                .setUtube("my_utube")
                .setTtl(1)
                .setTtr(1)
                .setDelay(0)
                .setPriority(0)
                .build()
                .runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);

        assertEquals(taken, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
        assertEquals(result, new TaskInfo<>(0, TaskStatus.READY, ttlTask));
    }

    @Test
    public void touch() {
        UtubeTtlTask ttlTask = new UtubeTtlTask("value");
        queue.put(ttlTask).runSync();
        TaskInfo<UtubeTtlTask> taken = queue.take().runSync();
        TaskInfo<UtubeTtlTask> result = queue.touch(0, 5).runSync();
        queue.ack(0).runSync();
        assertEquals(taken, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
        assertEquals(result, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
    }

    @Test
    public void touchAsync() throws InterruptedException, ExecutionException, TimeoutException {
        UtubeTtlTask ttlTask = new UtubeTtlTask("value");
        queue.put(ttlTask).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<UtubeTtlTask> taken = queue.take().runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<UtubeTtlTask> result = queue.touch(0, 5).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        queue.ack(0).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        assertEquals(taken, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
        assertEquals(result, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
    }
}
