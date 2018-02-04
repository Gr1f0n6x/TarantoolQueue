import org.junit.*
import org.tarantool.TarantoolClient
import org.tarantool.queue.*
import java.lang.UnsupportedOperationException

/**
 * Created by GrIfOn on 04.02.2018.
 */
//docker run --rm -p 3301:3301 -t -i tarantool/tarantool:1.7.6
class TarantoolQueueAsyncTest {
    @Before
    fun before() {
        queue.truncate()
    }

    @Test
    fun put() {
        val task = Task("task1")
        val result = queue.put(task)

        Assert.assertEquals(Tuple(0, TaskStatus.READY, task), result.get())
    }

    @Test
    fun take() {
        val task = Task("task1")
        queue.put(task)
        val result = queue.take(0)

        Assert.assertEquals(Tuple(0, TaskStatus.TAKEN, task), result.get())
    }

    @Test
    fun peek() {
        val task = Task("task1")
        val tuple = queue.put(task)
        val result = queue.peek(tuple.get().id)

        Assert.assertEquals(Tuple(0, TaskStatus.READY, task), result.get())
    }

    @Test
    fun release() {
        val task = Task("task1")
        val tuple = queue.put(task)
        queue.take(0)
        val result = queue.release(tuple.get().id)

        Assert.assertEquals(Tuple(0, TaskStatus.READY, task), result.get())
    }

    @Test
    fun ack() {
        val task = Task("task1")
        val tuple = queue.put(task)
        queue.take(0)
        val result = queue.ack(tuple.get().id)

        Assert.assertEquals(Tuple(0, TaskStatus.EXECUTED, task), result.get())
    }

    @Test
    fun delete() {
        val task = Task("task1")
        val tuple = queue.put(task)
        val result = queue.delete(tuple.get().id)

        Assert.assertEquals(Tuple(0, TaskStatus.EXECUTED, task), result.get())
    }

    @Test
    fun kick() {
        val task = Task("task1")
        val tuple = queue.put(task)
        queue.bury(tuple.get().id)
        val result = queue.kick(3)

        Assert.assertEquals(1, result.get())
    }

    @Test
    fun bury() {
        val task = Task("task1")
        val tuple = queue.put(task)
        val result = queue.bury(tuple.get().id)

        Assert.assertEquals(Tuple(0, TaskStatus.BURIED, task), result.get())
    }

    @Test(expected = UnsupportedOperationException::class)
    fun touch() {
        val task = Task("task1")
        val tuple = queue.put(task)
        queue.take(0)
        val result = queue.touch(tuple.get().id, 10)

        Assert.assertEquals(Tuple(0, TaskStatus.DELAYED, task), result.get())
    }

    @Test
    fun statistics() {
        val result = queue.statistics()
        Assert.assertEquals("{calls={take=0, kick=0, bury=0, release=0, ack=0, touch=0, delete=1, put=1}, tasks={buried=0, delayed=0, total=0, done=1, ready=0, taken=0}}",
                result.get().toString())
    }

    @Test
    fun ttl() {
        val q = TarantoolQueueAsync(client = connection, clazz = Task::class.java, queueName = "testttl", type = QueueType.FIFO_TTL)
        try {
            val task = Task(taskName = "ttltask")
            q.put(task, listOf(TTL(1)))
            Thread.sleep(2000)
            val take = q.take()
            Assert.assertEquals(Tuple(0, TaskStatus.BURIED, Task::class.java.newInstance()), take.get())
        } finally {
            q.drop()
        }
    }

    @Test
    fun priority() {
        val q = TarantoolQueueAsync(client = connection, clazz = Task::class.java, queueName = "testpriority", type = QueueType.FIFO_TTL)
        try {
            val task_one = Task(taskName = "ttltask_10")
            val task_two = Task(taskName = "ttltask_1")
            q.put(task_one, listOf(PRIORITY(10)))
            q.put(task_two, listOf(PRIORITY(1)))
            val take = q.take().get()

            Assert.assertEquals(task_two, take.task)
            q.ack(take.id)
        } finally {
            q.drop()
        }
    }

    @Test
    fun utube() {
        val q = TarantoolQueueAsync(client = connection, clazz = Task::class.java, queueName = "testutubeasync", type = QueueType.UTUBE)
        try {
            val task_one = Task(taskName = "ttltask_10")
            val task_two = Task(taskName = "ttltask_1")
            q.put(task_one, listOf(UTUBE("one")))
            q.put(task_two, listOf(UTUBE("two")))
            val take_one = q.take().get()
            val take_two = q.take().get()

            Assert.assertEquals(task_one, take_one.task)
            Assert.assertEquals(task_two, take_two.task)

            q.ack(take_one.id)
            q.ack(take_two.id)
        } finally {
            q.drop()
        }
    }

    companion object {
        lateinit var connection: TarantoolClient
        lateinit var queue: TarantoolQueueAsync<Task>

        @JvmStatic
        @BeforeClass
        fun beforeClass() {
            connection = TarantoolConnection.build("192.168.99.100", 3301)
            queue = TarantoolQueueAsync(client = connection, clazz = Task::class.java, queueName = "test", type = QueueType.FIFO)
        }

        @JvmStatic
        @AfterClass
        fun afterClass() {
            queue.drop()
        }
    }
}