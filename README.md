# TarantoolQueue

It is the wrapper for the [TarantoolQueue](https://github.com/tarantool/queue).
This library uses `Java annotation processing` to generate code for your tasks to be able to easy use your tasks with tarantool's queue.

## Features
- No reflection - only compile-time code generation 
- Supports different queue types: `FIFO`, `FIFO_TTL`, `LIM_FIFO_TTL` (queue module should be at least 1.0.4 version), `UTUBE` and `UTUBE_TTL`
- All methods are strongly typed
- For some queue types there are convenient methods with additional options

## Requirements
* Java 1.8 or higher
* Tarantool 1.7.6

## Getting Started

### Add a dependency to your `pom.xml` file.

```xml
        <dependency>
            <groupId>com.nryanov.tarantool</groupId>
            <artifactId>tarantool-queue</artifactId>
            <version>{version}</version>
        </dependency>
```

### Create a queue
Currently, `tarantool-queue` module does not support auto bootstraping queues in tarantool, so it is up to you to create initial queues with desired configuration.
For example, to create a simple fifo queue in tarantool:
```shell script
queue = require('queue')
queue.create_tube('fifo_queue', 'fifo', {if_not_exists=true})
``` 


Also, keep i mind that it is required to use exactly this name for `queue` module in tarantool: `queue = require('queue')`

### Create a simple POJO which represents task info:
```java
import org.tarantool.queue.QueueType;
import org.tarantool.queue.annotations.Queue;

@Queue(name = "fifo_queue", type = QueueType.FIFO)
class Task {
 // fields
}
```

`Queue` annotation will be used only in compile time to generate convenient wrapper for your task.  

QueueType can be `FIFO`, `FIFO_TTL`, `UTUBE`, `UTUBE_TTL`. The description of each type can be found there: [TarantoolQueue](https://github.com/tarantool/queue).
If your tarantool instance has installed `queue` module with version `1.0.4` or higher than you can also use `LIM_FIFO_TTL` queue type.

### Use
```java
public class Main {
    public static void main(String[] args) {    
        TarantoolClientConfig config = new TarantoolClientConfig();
        config.username = "guest";
        TarantoolClient client = new TarantoolClientImpl("localhost:3301", config);
        
        // also, you can specify custom ObjectMapper 
        TaskManagerFactory managerFactory = new TaskManagerFactory(client);
        TaskQueue taskQueue = managerFactory.taskQueueManager();
        
        Task task = new Task();
        TaskInfo<Task> result = queue.put(task).runSync();
        // ...
        TaskInfo<Task> nextTask = queue.take().runSync();
        // process(nextTask)
        queue.ack(nextTask.id).runSync();
    }
}
```

## Queue interface
```java
public interface QueueManager<T> {
    Operation<T> put(T task);

    Operation<T> release(long taskId);

    Operation<T> ack(long taskId);

    Operation<T> peek(long taskId);

    Operation<T> bury(long taskId);

    Operation<T> take();

    Operation<T> takeWithTimeout(long timeout);

    Operation<T> delete(long taskId);
}
```

Queues with types `FIFO_TTL` and `UTUBE_TTL` support additional method: `touch` to increase ttl of task. 

Queues with types `FIFO_TTL`, `UTUBE`, `UTUBE_TTL` and `LIM_FIFO_TTL` support additional methods: `putWithOptions` and `releaseWithOptions`. Each queue type has it's own builder with specific option list.

## Built With
* [Maven](https://maven.apache.org/) - Dependency Management