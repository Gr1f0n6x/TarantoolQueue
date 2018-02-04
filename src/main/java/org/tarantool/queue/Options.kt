package org.tarantool.queue

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

sealed class Options(val type: String)

data class PRIORITY(val value: Int): Options("pri") {
    override fun toString(): String {
        return "$type=$value"
    }
}

data class TTL(val value: Int): Options("ttl") {
    override fun toString(): String {
        return "$type=$value"
    }
}

data class TTR(val value: Int): Options("ttr") {
    override fun toString(): String {
        return "$type=$value"
    }
}

data class DELAY(val value: Int): Options("delay") {
    override fun toString(): String {
        return "$type=$value"
}
}

data class UTUBE(val value: String): Options("utube") {
    override fun toString(): String {
        return "$type='$value'"
    }
}