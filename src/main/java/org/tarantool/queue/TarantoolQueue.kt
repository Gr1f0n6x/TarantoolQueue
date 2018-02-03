package org.tarantool.queue

import com.fasterxml.jackson.databind.ObjectMapper
import org.tarantool.TarantoolClient

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
        println("queue.create_tube('$queueName', '${type.type}', {temporary=$temporary, if_not_exists=$ifNotExists ${if (onTaskChange != null) ", on_task_change=$onTaskChange" else "" }})")
        client.syncOps().eval("queue = require('queue'); queue.create_tube('$queueName', '${type.type}', {temporary=$temporary, if_not_exists=$ifNotExists ${if (onTaskChange != null) ", on_task_change=$onTaskChange" else "" }})")
    }

    override fun put(task: T): Tuple<T> {
        val json = mapper.writeValueAsString(task)
        val result = client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:put('$json')")[0] as List<Any>
        return deserialize(result)
    }

    override fun take(timeout: Int?): Tuple<T> {
        val result = client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:take()")[0] as List<Any>
        return deserialize(result)
    }

    override fun touch(taskId: Int, increment: Int): Tuple<T> {
        val result = client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:touch($taskId, $increment)")[0] as List<Any>
        return deserialize(result)
    }

    override fun ack(taskId: Int): Tuple<T> {
        val result = client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:ack($taskId)")[0] as List<Any>
        return deserialize(result)
    }

    override fun release(taskId: Int): Tuple<T> {
        val result = client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:release($taskId)")[0] as List<Any>
        return deserialize(result)
    }

    override fun peek(taskId: Int): Tuple<T> {
        val result = client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:peek($taskId)")[0] as List<Any>
        return deserialize(result)
    }

    override fun bury(taskId: Int): Tuple<T> {
        val result = client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:bury($taskId)")[0] as List<Any>
        return deserialize(result)
    }

    override fun kick(count: Long): Long =
            client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:kick($count)")[0] as Long

    override fun delete(taskId: Int): Tuple<T> {
        val result = client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:delete($taskId)")[0] as List<Any>
        return deserialize(result)
    }

    override fun drop(): Boolean =
            client.syncOps().eval("queue = require('queue'); return queue.tube.$queueName:drop()")[0] as Boolean


    override fun truncate() {
        client.syncOps().eval("queue = require('queue'); queue.tube.$queueName:truncate()")
    }

    fun statistics(): Map<String, Map<String, Int>> =
            client.syncOps().eval("queue = require('queue'); return queue.statistics('$queueName')")[0] as Map<String, Map<String, Int>>

    private fun deserialize(result: List<Any>): Tuple<T> {
        val task = mapper.readValue(result[2] as String, clazz)

        return Tuple(result[0] as Int, TaskStatus.getByValue(result[1] as String)!!, task)
    }

    companion object {
        val mapper = ObjectMapper()
    }
}