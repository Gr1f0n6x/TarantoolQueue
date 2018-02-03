package org.tarantool.queue

/**
 * Created by GrIfOn on 03.02.2018.
 */
enum class TaskStatus(val value: String) {
    READY("r"), TAKEN("t"), EXECUTED("-"), BURIED("!"), DELAYED("~");

    companion object {
        fun getByValue(value: String): TaskStatus? {
            return when(value) {
                "r" -> TaskStatus.READY
                "t" -> TaskStatus.TAKEN
                "-" -> TaskStatus.EXECUTED
                "!" -> TaskStatus.BURIED
                "~" -> TaskStatus.DELAYED
                else -> null
            }
        }
    }
}