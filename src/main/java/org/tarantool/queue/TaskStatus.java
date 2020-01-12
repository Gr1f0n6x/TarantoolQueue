package org.tarantool.queue;

public enum TaskStatus {
    READY("r"),
    TAKEN("t"),
    EXECUTED("-"),
    BURIED("!"),
    DELAYED("~");

    private String symbol;

    TaskStatus(String symbol) {
        this.symbol = symbol;
    }

    public static TaskStatus getBySymbol(String symbol) {
        switch (symbol) {
            case "r":
                return TaskStatus.READY;
            case "t":
                return TaskStatus.TAKEN;
            case "-":
                return TaskStatus.EXECUTED;
            case "!":
                return TaskStatus.BURIED;
            case "~":
                return TaskStatus.DELAYED;
            default:
                throw new IllegalArgumentException("Unknown symbol of task status: " + symbol);
        }
    }
}
