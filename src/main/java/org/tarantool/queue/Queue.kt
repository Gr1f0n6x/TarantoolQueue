package org.tarantool.queue

/**
 * Created by GrIfOn on 03.02.2018.
 */
interface Queue<T> {
    fun put(task: T): Tuple<T> // TODO: options
    fun take(timeout: Int?): Tuple<T>
    fun touch(taskId: Int, increment: Int): Tuple<T>
    fun ack(taskId: Int): Tuple<T>
    fun release(taskId: Int): Tuple<T> //TODO: options
    fun peek(taskId: Int): Tuple<T>
    fun bury(taskId: Int): Tuple<T>
    fun kick(count: Long): Long
    fun delete(taskId: Int): Tuple<T>
    fun drop(): Boolean
    fun truncate()
}