package org.tarantool.queue.integration;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tarantool.TarantoolException;
import org.tarantool.queue.TaskInfo;
import org.tarantool.queue.TaskStatus;
import org.tarantool.queue.generated.FifoTaskQueue;
import org.tarantool.queue.integration.tasks.FifoTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class FifoTaskQueueTest extends IntegrationTest {
    public static FifoTaskQueue queue;

    @BeforeClass
    public static void setUpQueue() {
        client.syncOps().eval( "queue.create_tube('fifo_queue', 'fifo', {if_not_exists=true})");
        queue = managerFactory.fifoTaskQueueManager();
    }

    @AfterClass
    public static void dropQueue() {
        client.syncOps().eval("queue.tube.fifo_queue:drop()");
    }

    @After
    public void truncate() {
        client.syncOps().eval("queue.tube.fifo_queue:truncate()");
    }

    @Test
    public void put() {
        FifoTask task = new FifoTask("task info");
        TaskInfo<FifoTask> result = queue.put(task).runSync();
        assertEquals(result, new TaskInfo<>(0, TaskStatus.READY, task));
    }

    @Test
    public void putAsync() throws InterruptedException, ExecutionException, TimeoutException {
        FifoTask task = new FifoTask("task info");
        TaskInfo<FifoTask> result = queue.put(task).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        assertEquals(result, new TaskInfo<>(0, TaskStatus.READY, task));
    }

    @Test
    public void release() {
        FifoTask task = new FifoTask("task info");
        queue.put(task).runSync();
        TaskInfo<FifoTask> take = queue.take().runSync();
        TaskInfo<FifoTask> result = queue.release(0).runSync();
        assertEquals(take, new TaskInfo<>(0, TaskStatus.TAKEN, task));
        assertEquals(result, new TaskInfo<>(0, TaskStatus.READY, task));
    }

    @Test
    public void releaseAsync() throws InterruptedException, ExecutionException, TimeoutException {
        FifoTask task = new FifoTask("task info");
        queue.put(task).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<FifoTask> take = queue.take().runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<FifoTask> result = queue.release(0).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        assertEquals(take, new TaskInfo<>(0, TaskStatus.TAKEN, task));
        assertEquals(result, new TaskInfo<>(0, TaskStatus.READY, task));
    }

    @Test
    public void ack() {
        FifoTask task = new FifoTask("task info");
        queue.put(task).runSync();
        TaskInfo<FifoTask> take = queue.take().runSync();
        TaskInfo<FifoTask> result = queue.ack(0).runSync();
        assertEquals(take, new TaskInfo<>(0, TaskStatus.TAKEN, task));
        assertEquals(result, new TaskInfo<>(0, TaskStatus.EXECUTED, task));
    }

    @Test
    public void ackAsync() throws InterruptedException, ExecutionException, TimeoutException {
        FifoTask task = new FifoTask("task info");
        queue.put(task).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<FifoTask> take = queue.take().runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<FifoTask> result = queue.ack(0).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        assertEquals(take, new TaskInfo<>(0, TaskStatus.TAKEN, task));
        assertEquals(result, new TaskInfo<>(0, TaskStatus.EXECUTED, task));
    }

    @Test
    public void peek() {
        FifoTask task = new FifoTask("task info");
        queue.put(task).runSync();
        TaskInfo<FifoTask> result = queue.peek(0).runSync();
        assertEquals(result, new TaskInfo<>(0, TaskStatus.READY, task));
    }

    @Test
    public void peekAsync() throws InterruptedException, ExecutionException, TimeoutException {
        FifoTask task = new FifoTask("task info");
        queue.put(task).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<FifoTask> result = queue.peek(0).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        assertEquals(result, new TaskInfo<>(0, TaskStatus.READY, task));
    }


    @Test
    public void bury() {
        FifoTask task = new FifoTask("task info");
        queue.put(task).runSync();
        TaskInfo<FifoTask> result = queue.bury(0).runSync();
        assertEquals(result, new TaskInfo<>(0, TaskStatus.BURIED, task));
    }

    @Test
    public void buryAsync() throws InterruptedException, ExecutionException, TimeoutException {
        FifoTask task = new FifoTask("task info");
        queue.put(task).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<FifoTask> result = queue.bury(0).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        assertEquals(result, new TaskInfo<>(0, TaskStatus.BURIED, task));
    }

    @Test
    public void take() {
        FifoTask task = new FifoTask("task info");
        queue.put(task).runSync();
        TaskInfo<FifoTask> take = queue.take().runSync();
        queue.ack(0).runSync();
        assertEquals(take, new TaskInfo<>(0, TaskStatus.TAKEN, task));
    }

    @Test
    public void takeAsync() throws InterruptedException, ExecutionException, TimeoutException {
        FifoTask task = new FifoTask("task info");
        queue.put(task).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<FifoTask> take = queue.take().runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        queue.ack(0).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        assertEquals(take, new TaskInfo<>(0, TaskStatus.TAKEN, task));
    }

    @Test
    public void takeEmpty() {
        TaskInfo<FifoTask> take = queue.take().runSync();
        assertNull(take);
    }

    @Test
    public void takeEmptyAsync() throws InterruptedException, ExecutionException, TimeoutException {
        TaskInfo<FifoTask> take = queue.take().runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        assertNull(take);
    }

    @Test
    public void takeWithTimeoutEmpty() {
        TaskInfo<FifoTask> take = queue.takeWithTimeout(1).runSync();
        assertNull(take);
    }

    @Test
    public void takeWithTimeoutEmptyAsync() throws InterruptedException, ExecutionException, TimeoutException {
        TaskInfo<FifoTask> take = queue.takeWithTimeout(1).runAsync().toCompletableFuture().get(2, TimeUnit.SECONDS);
        assertNull(take);
    }

    @Test
    public void takeWithTimeout() {
        FifoTask task = new FifoTask("task info");
        queue.put(task).runSync();
        TaskInfo<FifoTask> take = queue.takeWithTimeout(1).runSync();
        queue.ack(0).runSync();
        assertEquals(take, new TaskInfo<>(0, TaskStatus.TAKEN, task));
    }

    @Test
    public void takeWithTimeoutAsync() throws InterruptedException, ExecutionException, TimeoutException {
        FifoTask task = new FifoTask("task info");
        queue.put(task).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<FifoTask> take = queue.takeWithTimeout(1).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        queue.ack(0).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        assertEquals(take, new TaskInfo<>(0, TaskStatus.TAKEN, task));
    }

    // throws error that task with id not found
    @Test(expected = TarantoolException.class)
    public void delete() {
        FifoTask task = new FifoTask("task info");
        queue.put(task).runSync();
        TaskInfo<FifoTask> result = queue.delete(0).runSync();
        assertEquals(result, new TaskInfo<>(0, TaskStatus.EXECUTED, task));
        queue.peek(0).runSync();
    }

    // throws error that task with id not found
    @Test(expected = ExecutionException.class)
    public void deleteAsync() throws InterruptedException, ExecutionException, TimeoutException {
        FifoTask task = new FifoTask("task info");
        queue.put(task).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        TaskInfo<FifoTask> result = queue.delete(0).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
        assertEquals(result, new TaskInfo<>(0, TaskStatus.EXECUTED, task));
        queue.peek(0).runAsync().toCompletableFuture().get(1, TimeUnit.SECONDS);
    }
}
