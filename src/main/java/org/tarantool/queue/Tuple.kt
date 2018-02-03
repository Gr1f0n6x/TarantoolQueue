package org.tarantool.queue

/**
 * Created by GrIfOn on 03.02.2018.
 */
data class Tuple<T>(val id: Int, val status: TaskStatus, val task: T)