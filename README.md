# TarantoolQueue

It is the wrapper for the [TarantoolQueue](https://github.com/tarantool/queue)

## Requirements
* Java 1.8 or higher
* Tarantool 1.7.6

## Getting Started

1. Add a dependency to your `pom.xml` file.

```xml
        <dependency>
            <groupId>com.github.Gr1f0n6x</groupId>
            <artifactId>tarantoolqueue</artifactId>
            <version>0.3.1</version>
        </dependency>
```

2. Create a queue
```java
    queue = TarantoolQueueAsync(client = client, clazz = Task::class.java, queueName = "test", type = QueueType.FIFO)
```

Where task is simple POJO class:
```java
class Task {
    private String taskName;
    
    Task() {}
    
    Task(String taskName) {
        this.taskName = taskName;
    }
}
```

QueueType can be FIFO, FIFOTTL, UTUBE, UTUBETTL. The description of each type can be found there: [TarantoolQueue](https://github.com/tarantool/queue)

3. Use your queue
 
 ```java
Tuple<Task> tuple = queue.put(new Task("task1")); // [0. 'r', {'taskName': 'task1'}]
tuple = queue.take(); // [0. 't', {'taskName': 'task1'}]
tuple = queue.ack(0); // [0. '-', {'taskName': 'task1'}]
```

Some methods can accept additional list of options:
```java
List<Options> options = new ArrayList<>();
options.add(TTL(1));
options.add(TTR(1));
options.add(PRIORITY(1));
options.add(DELAY(1));
options.add(UTUBE("subqueue"));
queue.put(new Task("task1"), options);
```

## Queue interface
```kotlin
interface Queue<T> {
    fun put(task: T, options: List<Options>? = null): ResultSet<Tuple<T>>
    fun take(timeout: Int? = 0): ResultSet<Tuple<T>>
    fun touch(taskId: Int, increment: Int): ResultSet<Tuple<T>>
    fun ack(taskId: Int): ResultSet<Tuple<T>>
    fun release(taskId: Int, options: List<Options>? = null): ResultSet<Tuple<T>>
    fun peek(taskId: Int): ResultSet<Tuple<T>>
    fun bury(taskId: Int): ResultSet<Tuple<T>>
    fun kick(count: Long): ResultSet<Long>
    fun delete(taskId: Int): ResultSet<Tuple<T>>
    fun drop(): ResultSet<Boolean>
    fun truncate(): Unit
}
```


## Built With

* [Maven](https://maven.apache.org/) - Dependency Management