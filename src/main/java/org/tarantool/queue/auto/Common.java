package org.tarantool.queue.auto;

final class Common {
    public static String PACKAGE_NAME = "org.tarantool.queue.generated";

    public static String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}
