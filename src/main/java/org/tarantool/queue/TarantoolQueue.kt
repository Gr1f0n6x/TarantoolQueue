package org.tarantool.queue

import com.fasterxml.jackson.databind.ObjectMapper
import org.tarantool.TarantoolClient
import java.lang.UnsupportedOperationException

/**
MIT License

Copyright (c) 2018 Nick

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


class TarantoolQueue<T>(val client: TarantoolClient,
                        val clazz: Class<T>,
                        val type: QueueType = QueueType.FIFO,
                        val queueName: String,
                        val temporary: Boolean = true,
                        val ifNotExists: Boolean = true,
                        val onTaskChange: String? = null): Queue<T> {

    init {
        client.syncOps().eval("queue = require('queue'); queue.create_tube('$queueName', '${type.type}', {temporary=$temporary, if_not_exists=$ifNotExists ${if (onTaskChange != null) ", on_task_change=$onTaskChange" else "" }})")
    }

    override fun put(task: T, options: List<Options>?): ResultSet<Tuple<T>> {
        val json = mapper.writeValueAsString(task)
        if (options != null && options.isNotEmpty()) {
            return ResultSetTaskImpl(client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:put('$json', {${options.joinToString(separator = ", ")}})"))
        }
        return ResultSetTaskImpl(client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:put('$json')"))
    }

    override fun take(timeout: Int?): ResultSet<Tuple<T>> =
            ResultSetTaskImpl(client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:take($timeout)"))

    override fun touch(taskId: Int, increment: Int): ResultSet<Tuple<T>> {
        if (type != QueueType.FIFO_TTL && type != QueueType.UTUBE_TTL) throw UnsupportedOperationException("Only fifottl and utubettl support this operation")
        return ResultSetTaskImpl(client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:touch($taskId, $increment)"))
    }

    override fun ack(taskId: Int): ResultSet<Tuple<T>> =
            ResultSetTaskImpl(client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:ack($taskId)"))

    override fun release(taskId: Int, options: List<Options>?): ResultSet<Tuple<T>> {
        if (options != null && options.isNotEmpty()) {
            return ResultSetTaskImpl(client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:release($taskId, {${options.joinToString(separator = ", ")}})"))
        }
        return ResultSetTaskImpl(client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:release($taskId)"))
    }

    override fun peek(taskId: Int): ResultSet<Tuple<T>> =
            ResultSetTaskImpl(client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:peek($taskId)"))

    override fun bury(taskId: Int): ResultSet<Tuple<T>> =
            ResultSetTaskImpl(client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:bury($taskId)"))

    override fun kick(count: Long): ResultSet<Long> =
            ResultSetImpl(client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:kick($count)"))

    override fun delete(taskId: Int): ResultSet<Tuple<T>> =
            ResultSetTaskImpl(client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:delete($taskId)"))

    override fun drop(): ResultSet<Boolean> =
            ResultSetImpl(client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:drop()"))

    override fun truncate() {
        client.syncOps().eval("queue = require('queue'); queue.tube.$queueName:truncate()")
    }

    fun statistics(): ResultSet<Map<String, Map<String, Int>>> =
            ResultSetImpl(client.syncOps().eval("queue = require('queue'); return queue.statistics('$queueName')"))

    private inner class ResultSetTaskImpl(val result: List<*>): ResultSet<Tuple<T>> {
        override fun get(): Tuple<T> =
                if (result.isNotEmpty()) deserialize(result[0] as List<*>) else Tuple(0, TaskStatus.BURIED, clazz.newInstance())

        private fun deserialize(result: List<*>): Tuple<T> {
            val task = mapper.readValue(result[2] as String, clazz)
            return Tuple(result[0] as Int, TaskStatus.getByValue(result[1] as String)!!, task)
        }
    }

    private inner class ResultSetImpl<R>(val result: List<*>): ResultSet<R> {
        override fun get(): R =
                if (result.isNotEmpty()) result[0] as R else null!!
    }

    companion object {
        val mapper = ObjectMapper()
    }
}