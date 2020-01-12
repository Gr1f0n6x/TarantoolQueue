package org.tarantool.queue.auto;

import com.google.common.base.Joiner;
import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.junit.Test;
import org.tarantool.queue.auto.data.*;

import javax.tools.JavaFileObject;
import java.util.Arrays;

public class QueueProcessorTest {
    public static final String NEW_LINE = System.getProperty("line.separator");

    private final JavaFileObject managerFactoryOutput = JavaFileObjects.forSourceString(
            "org.tarantool.orm.generated.ManagerFactory",
            ManagerFactoryOutput.output
    );

    @Test
    public void incorrectAnnotatedTypeError() {
        final JavaFileObject input = JavaFileObjects.forSourceString(
                "test.Task",
                Joiner.on(NEW_LINE).join(
                        "package test;",
                        "",
                        "import org.tarantool.queue.annotations.*;",
                        "import org.tarantool.queue.QueueType;",
                        "@Queue(name = \"queue\", type = QueueType.FIFO)",
                        "public enum Task {}"
                )
        );

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(Arrays.asList(input))
                .processedWith(new QueueProcessor())
                .failsToCompile()
                .withErrorContaining("Only classes and interfaces may be annotated by Queue");
    }

    @Test
    public void emptyQueueNameError() {
        final JavaFileObject input = JavaFileObjects.forSourceString(
                "test.Task",
                Joiner.on(NEW_LINE).join(
                        "package test;",
                        "",
                        "import org.tarantool.queue.annotations.*;",
                        "import org.tarantool.queue.QueueType;",
                        "@Queue(name = \"\", type = QueueType.FIFO)",
                        "public class Task {}"
                )
        );

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(Arrays.asList(input))
                .processedWith(new QueueProcessor())
                .failsToCompile()
                .withErrorContaining("Queue name should not be empty");
    }


    @Test
    public void fifoQueueTask() {
        final JavaFileObject input = JavaFileObjects.forSourceString(
                "test.Task",
                Joiner.on(NEW_LINE).join(
                        "package test;",
                        "",
                        "import org.tarantool.queue.annotations.*;",
                        "import org.tarantool.queue.QueueType;",
                        "@Queue(name = \"queue\", type = QueueType.FIFO)",
                        "public class Task {}"
                )
        );

        final JavaFileObject taskQueueClass = JavaFileObjects.forSourceString(
                "org.tarantool.queue.generated.TaskQueue",
                FifoOutput.output
        );

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(Arrays.asList(input))
                .processedWith(new QueueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(taskQueueClass, managerFactoryOutput);
    }

    @Test
    public void fifoTtlQueueTask() {
        final JavaFileObject input = JavaFileObjects.forSourceString(
                "test.Task",
                Joiner.on(NEW_LINE).join(
                        "package test;",
                        "",
                        "import org.tarantool.queue.annotations.*;",
                        "import org.tarantool.queue.QueueType;",
                        "@Queue(name = \"queue\", type = QueueType.FIFO_TTL)",
                        "public class Task {}"
                )
        );

        final JavaFileObject taskQueueClass = JavaFileObjects.forSourceString(
                "org.tarantool.queue.generated.TaskQueue",
                FifoTtlOutput.output
        );

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(Arrays.asList(input))
                .processedWith(new QueueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(taskQueueClass, managerFactoryOutput);
    }

    @Test
    public void limFifoTtlQueueTask() {
        final JavaFileObject input = JavaFileObjects.forSourceString(
                "test.Task",
                Joiner.on(NEW_LINE).join(
                        "package test;",
                        "",
                        "import org.tarantool.queue.annotations.*;",
                        "import org.tarantool.queue.QueueType;",
                        "@Queue(name = \"queue\", type = QueueType.LIM_FIFO_TTL)",
                        "public class Task {}"
                )
        );

        final JavaFileObject taskQueueClass = JavaFileObjects.forSourceString(
                "org.tarantool.queue.generated.TaskQueue",
                LimFifoOutput.output
        );

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(Arrays.asList(input))
                .processedWith(new QueueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(taskQueueClass, managerFactoryOutput);
    }

    @Test
    public void utubeQueueTask() {
        final JavaFileObject input = JavaFileObjects.forSourceString(
                "test.Task",
                Joiner.on(NEW_LINE).join(
                        "package test;",
                        "",
                        "import org.tarantool.queue.annotations.*;",
                        "import org.tarantool.queue.QueueType;",
                        "@Queue(name = \"queue\", type = QueueType.UTUBE)",
                        "public class Task {}"
                )
        );

        final JavaFileObject taskQueueClass = JavaFileObjects.forSourceString(
                "org.tarantool.queue.generated.TaskQueue",
                UtubeOutput.output
        );

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(Arrays.asList(input))
                .processedWith(new QueueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(taskQueueClass, managerFactoryOutput);
    }

    @Test
    public void utubeTtlQueueTask() {
        final JavaFileObject input = JavaFileObjects.forSourceString(
                "test.Task",
                Joiner.on(NEW_LINE).join(
                        "package test;",
                        "",
                        "import org.tarantool.queue.annotations.*;",
                        "import org.tarantool.queue.QueueType;",
                        "@Queue(name = \"queue\", type = QueueType.UTUBE_TTL)",
                        "public class Task {}"
                )
        );

        final JavaFileObject taskQueueClass = JavaFileObjects.forSourceString(
                "org.tarantool.queue.generated.TaskQueue",
                UtubeTtlOutput.output
        );

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(Arrays.asList(input))
                .processedWith(new QueueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(taskQueueClass, managerFactoryOutput);
    }
}
