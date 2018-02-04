package org.tarantool.queue

import org.tarantool.SocketChannelProvider
import org.tarantool.TarantoolClient
import org.tarantool.TarantoolClientConfig
import org.tarantool.TarantoolClientImpl

import java.net.InetSocketAddress
import java.nio.channels.SocketChannel

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

object TarantoolConnection {
    fun build(host: String, port: Int): TarantoolClient = TarantoolClientImpl(socketChannelProvider(host, port), TarantoolClientConfig().apply {
        this.username = "guest"
    })

    fun build(username: String, password: String, host: String, port: Int): TarantoolClient = TarantoolClientImpl(socketChannelProvider(host, port), TarantoolClientConfig().apply {
        this.username = username
        this.password = password
    })

    fun build(sockerProvider: SocketChannelProvider, config: TarantoolClientConfig): TarantoolClient = TarantoolClientImpl(sockerProvider, config)

    private fun socketChannelProvider(host: String, port: Int): SocketChannelProvider = SocketChannelProvider { retryNumber, lastError ->
        lastError?.printStackTrace()
        SocketChannel.open(InetSocketAddress(host, port))
    }
}