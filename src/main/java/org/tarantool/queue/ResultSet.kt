package org.tarantool.queue

/**
 * Created by GrIfOn on 04.02.2018.
 */
interface ResultSet<T> {
    fun get(): T
}