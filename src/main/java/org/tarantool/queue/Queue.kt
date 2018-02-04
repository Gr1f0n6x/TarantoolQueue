package org.tarantool.queue

/**
 * Created by GrIfOn on 03.02.2018.
 */
interface Queue<T> {
    fun put(task: T): ResultSet<Tuple<T>> // TODO: options
    fun take(timeout: Int?): ResultSet<Tuple<T>>
    fun touch(taskId: Int, increment: Int): ResultSet<Tuple<T>>
    fun ack(taskId: Int): ResultSet<Tuple<T>>
    fun release(taskId: Int): ResultSet<Tuple<T>> //TODO: options
    fun peek(taskId: Int): ResultSet<Tuple<T>>
    fun bury(taskId: Int): ResultSet<Tuple<T>>
    fun kick(count: Long): ResultSet<Long>
    fun delete(taskId: Int): ResultSet<Tuple<T>>
    fun drop(): ResultSet<Boolean>
    fun truncate(): Unit
}

