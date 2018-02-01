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


class TarantoolTTLUtube(client: TarantoolClient, queueName: String, temporary: Boolean = true, ifNotExists: Boolean = true, onTaskChange: String?):
        TarantoolQueue(client, QueueType.UTUBE_TTL, queueName, temporary, ifNotExists, onTaskChange) {
    override fun put() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun take() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun touch() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun ack() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun release() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun peek() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun bury() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun kick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun drop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}