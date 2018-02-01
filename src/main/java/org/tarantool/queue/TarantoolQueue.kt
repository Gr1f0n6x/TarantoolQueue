package org.tarantool.queue

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


abstract class TarantoolQueue(val client: TarantoolClient, val type: QueueType, val queueName: String, val temporary: Boolean = true, val ifNotExists: Boolean = true, val onTaskChange: String?) {
    init {
        println("queue.create_tube('$queueName', '${type.type}', {temporary=$temporary, if_not_exists=$ifNotExists ${if (onTaskChange != null) ", on_task_change=$onTaskChange" else "" }})")
        client.syncOps().eval("queue = require('queue'); queue.create_tube('$queueName', '${type.type}', {temporary=$temporary, if_not_exists=$ifNotExists ${if (onTaskChange != null) ", on_task_change=$onTaskChange" else "" }})")
    }

    abstract fun put()
    abstract fun take()
    abstract fun touch()
    abstract fun ack()
    abstract fun release()
    abstract fun peek()
    abstract fun bury()
    abstract fun kick()
    abstract fun delete()
    abstract fun drop()

    fun statistics() = client.syncOps().eval("queue = require('queue'); return queue.statistics('$queueName')")

    companion object {
        fun build(client: TarantoolClient, type: QueueType = QueueType.FIFO, queueName: String, temporary: Boolean = true, ifNotExists: Boolean = true, onTaskChange: String? = null): TarantoolQueue {
            return when (type) {
                QueueType.FIFO -> TarantoolSimpleQueue(client, queueName, temporary, ifNotExists, onTaskChange)
                QueueType.FIFO_TTL -> TarantoolTTLQueue(client, queueName, temporary, ifNotExists, onTaskChange)
                QueueType.UTUBE -> TarantoolSimpleUtube(client, queueName, temporary, ifNotExists, onTaskChange)
                QueueType.UTUBE_TTL -> TarantoolTTLUtube(client, queueName, temporary, ifNotExists, onTaskChange)
            }
        }
    }
}