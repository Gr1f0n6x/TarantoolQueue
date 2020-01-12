package org.tarantool.queue.integration;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tarantool.queue.TaskInfo;
import org.tarantool.queue.TaskStatus;
import org.tarantool.queue.generated.UtubeTaskQueue;
import org.tarantool.queue.integration.tasks.UtubeTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class UtubeTaskQueueTest extends IntegrationTest {
    public static UtubeTaskQueue queue;

    @BeforeClass
    public static void setUpQueue() {
        client.syncOps().eval("queue.create_tube('utube_queue', 'utube', {if_not_exists=true})");
        queue = managerFactory.utubeTaskQueueManager();
    }

    @AfterClass
    public static void dropQueue() {
        client.syncOps().eval("queue.tube.utube_queue:drop()");
    }

    @After
    public void truncate() {
        client.syncOps().eval("queue.tube.utube_queue:truncate()");
    }

    @Test
    public void putWithOptions() {
        UtubeTask ttlTask = new UtubeTask("value");
        TaskInfo<UtubeTask> result = queue
                .putWithOptions(ttlTask)
                .setUtube("my_utube")
                .build()
                .runSync();

        assertEquals(result, new TaskInfo<>(0, TaskStatus.READY, ttlTask));
    }

    @Test
    public void putWithOptionsAsync() throws InterruptedException, ExecutionException, TimeoutException {
        UtubeTask ttlTask = new UtubeTask("value");
        TaskInfo<UtubeTask> result = queue
                .putWithOptions(ttlTask)
                .setUtube("my_utube")
                .build()
                .runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);

        assertEquals(result, new TaskInfo<>(0, TaskStatus.READY, ttlTask));
    }

    @Test
    public void releaseWithOptions() {
        UtubeTask ttlTask = new UtubeTask("value");
        queue.put(ttlTask).runSync();
        TaskInfo<UtubeTask> taken = queue.take().runSync();
        TaskInfo<UtubeTask> result = queue
                .releaseWithOptions(0)
                .setUtube("my_utube")
                .build()
                .runSync();

        assertEquals(taken, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
        assertEquals(result, new TaskInfo<>(0, TaskStatus.READY, ttlTask));
    }

    @Test
    public void releaseWithOptionsAsync() throws InterruptedException, ExecutionException, TimeoutException {
        UtubeTask ttlTask = new UtubeTask("value");
        queue.put(ttlTask).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<UtubeTask> taken = queue.take().runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<UtubeTask> result = queue
                .releaseWithOptions(0)
                .setUtube("my_utube")
                .build()
                .runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);

        assertEquals(taken, new TaskInfo<>(0, TaskStatus.TAKEN, ttlTask));
        assertEquals(result, new TaskInfo<>(0, TaskStatus.READY, ttlTask));
    }
}
