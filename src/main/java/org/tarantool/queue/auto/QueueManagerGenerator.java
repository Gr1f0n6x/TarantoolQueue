package org.tarantool.queue.auto;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.squareup.javapoet.*;
import org.tarantool.TarantoolClient;
import org.tarantool.queue.TaskInfo;
import org.tarantool.queue.TaskStatus;
import org.tarantool.queue.internals.Meta;
import org.tarantool.queue.internals.operations.EvalOperation;
import org.tarantool.queue.internals.operations.Operation;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.IOException;

abstract class QueueManagerGenerator {
    private final Filer filer;
    protected final QueueMeta queueMeta;
    protected final TypeName operationResultType;

    public static QueueManagerGenerator getInstance(Filer filer, QueueMeta queueMeta) {
        switch (queueMeta.queueType) {
            case FIFO:
                return new FifoQueueManagerGenerator(filer, queueMeta);
            case FIFO_TTL:
                return new FifoTtlQueueManagerGenerator(filer, queueMeta);
            case LIM_FIFO_TTL:
                return new LimFifoTtlQueueManagerGenerator(filer, queueMeta);
            case UTUBE:
                return new UtubeQueueManagerGenerator(filer, queueMeta);
            case UTUBE_TTL:
                return new UtubeTtlQueueManagerGenerator(filer, queueMeta);
            default:
                throw new RuntimeException("Unknown queue type");
        }
    }

    public QueueManagerGenerator(Filer filer, QueueMeta queueMeta) {
        this.filer = filer;
        this.queueMeta = queueMeta;
        this.operationResultType = ParameterizedTypeName.get(ClassName.get(Operation.class), ClassName.get(Common.PACKAGE_NAME, queueMeta.taskManagerName));
    }

    public final void generate() throws IOException {
        TaskMetaGenerator metaGenerator = new TaskMetaGenerator(queueMeta);
        TypeSpec metaSpec = metaGenerator.generate();

        JavaFile javaFile = JavaFile
                .builder(Common.PACKAGE_NAME, queueBuilder(metaSpec).build())
                .build();

        javaFile.writeTo(filer);
    }

    protected TypeSpec.Builder queueBuilder(TypeSpec metaSpec) {
        return TypeSpec
                .classBuilder(queueMeta.taskManagerName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(queueNameField(queueMeta))
                .addField(TarantoolClient.class, "tarantoolClient", Modifier.PRIVATE, Modifier.FINAL)
                .addField(ObjectReader.class, "reader", Modifier.PRIVATE, Modifier.FINAL)
                .addField(ObjectWriter.class, "writer", Modifier.PRIVATE, Modifier.FINAL)
                .addField(metaGeneratorField(queueMeta, metaSpec))
                .addType(metaSpec)
                .addMethod(generateConstructor())
                .addMethod(generatePut())
                .addMethod(generateRelease())
                .addMethod(generateAck())
                .addMethod(generatePeek())
                .addMethod(generateBury())
                .addMethod(generateTake())
                .addMethod(generateTakeWithTimeout())
                .addMethod(generateDelete());
    }

    private FieldSpec queueNameField(QueueMeta queueMeta) {
        return FieldSpec
                .builder(String.class, "queueName", Modifier.PRIVATE, Modifier.FINAL)
                .initializer("$S", queueMeta.queueName)
                .build();
    }

    private FieldSpec metaGeneratorField(QueueMeta queueMeta, TypeSpec metaSpec) {
        return FieldSpec
                .builder(ParameterizedTypeName.get(ClassName.get(Meta.class), queueMeta.classType), "meta")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $L()", metaSpec.name)
                .build();
    }

    private MethodSpec generateConstructor() {
        return MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TarantoolClient.class, "tarantoolClient", Modifier.FINAL)
                .addParameter(ObjectReader.class, "reader", Modifier.FINAL)
                .addParameter(ObjectWriter.class, "writer", Modifier.FINAL)
                .addStatement("this.$N = $N", "tarantoolClient", "tarantoolClient")
                .addStatement("this.$N = $N", "reader", "reader")
                .addStatement("this.$N = $N", "writer", "writer")
                .build();
    }

    private MethodSpec generatePut() {
        return MethodSpec
                .methodBuilder("put")
                .addModifiers(Modifier.PUBLIC)
                .returns(operationResultType)
                .addParameter(queueMeta.classType, "task", Modifier.FINAL)
                .beginControlFlow("try")
                .addStatement("$T taskJson = writer.writeValueAsString(task)", String.class)
                .addStatement("return new $T<>(tarantoolClient, meta, $T.format($S, queueName, taskJson))", EvalOperation.class, String.class, "return queue.tube.%s:put(%s)")
                .nextControlFlow("catch ($T e)", Exception.class)
                .addStatement("throw new $T(e)", RuntimeException.class)
                .endControlFlow()
                .build();
    }

    private MethodSpec generateRelease() {
        return MethodSpec
                .methodBuilder("release")
                .addModifiers(Modifier.PUBLIC)
                .returns(operationResultType)
                .addParameter(long.class, "taskId", Modifier.FINAL)
                .addStatement("return new $T<>(tarantoolClient, meta, $T.format($S, queueName, taskId))", EvalOperation.class, String.class, "return queue.tube.%s:release(%s)")
                .build();
    }

    private MethodSpec generateTake() {
        return MethodSpec
                .methodBuilder("take")
                .addModifiers(Modifier.PUBLIC)
                .returns(operationResultType)
                .addParameter(long.class, "taskId", Modifier.FINAL)
                .addStatement("return new $T<>(tarantoolClient, meta, $T.format($S, queueName, taskId))", EvalOperation.class, String.class, "return queue.tube.%s:take(%s)")
                .build();
    }

    private MethodSpec generateTakeWithTimeout() {
        return MethodSpec
                .methodBuilder("takeWithTimeout")
                .addModifiers(Modifier.PUBLIC)
                .returns(operationResultType)
                .addParameter(long.class, "taskId", Modifier.FINAL)
                .addParameter(long.class, "timeout", Modifier.FINAL)
                .addStatement("return new $T<>(tarantoolClient, meta, $T.format($S, queueName, taskId, timeout))", EvalOperation.class, String.class, "return queue.tube.%s:take(%s, %s)")
                .build();
    }

    private MethodSpec generateAck() {
        return MethodSpec
                .methodBuilder("ack")
                .addModifiers(Modifier.PUBLIC)
                .returns(operationResultType)
                .addParameter(long.class, "taskId", Modifier.FINAL)
                .addStatement("return new $T<>(tarantoolClient, meta, $T.format($S, queueName, taskId))", EvalOperation.class, String.class, "return queue.tube.%s:ack(%s)")
                .build();
    }

    private MethodSpec generatePeek() {
        return MethodSpec
                .methodBuilder("peek")
                .addModifiers(Modifier.PUBLIC)
                .returns(operationResultType)
                .addParameter(long.class, "taskId", Modifier.FINAL)
                .addStatement("return new $T<>(tarantoolClient, meta, $T.format($S, queueName, taskId))", EvalOperation.class, String.class, "return queue.tube.%s:peek(%s)")
                .build();
    }

    private MethodSpec generateBury() {
        return MethodSpec
                .methodBuilder("bury")
                .addModifiers(Modifier.PUBLIC)
                .returns(operationResultType)
                .addParameter(long.class, "taskId", Modifier.FINAL)
                .addStatement("return new $T<>(tarantoolClient, meta, $T.format($S, queueName, taskId))", EvalOperation.class, String.class, "return queue.tube.%s:bury(%s)")
                .build();
    }

    private MethodSpec generateDelete() {
        return MethodSpec
                .methodBuilder("delete")
                .addModifiers(Modifier.PUBLIC)
                .returns(operationResultType)
                .addParameter(long.class, "taskId", Modifier.FINAL)
                .addStatement("return new $T<>(tarantoolClient, meta, $T.format($S, queueName, taskId))", EvalOperation.class, String.class, "return queue.tube.%s:delete(%s)")
                .build();
    }

    private static class TaskMetaGenerator {
        private final ClassName list = ClassName.get("java.util", "List");
        private final ParameterizedTypeName wildCardList = ParameterizedTypeName.get(list, WildcardTypeName.subtypeOf(Object.class));
        private final QueueMeta queueMeta;

        public TaskMetaGenerator(QueueMeta queueMeta) {
            this.queueMeta = queueMeta;
        }

        public TypeSpec generate() {
            return TypeSpec
                    .classBuilder(Common.capitalize(queueMeta.className) + "Meta")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addField(ObjectReader.class, "reader", Modifier.PRIVATE, Modifier.FINAL)
                    .addField(ObjectWriter.class, "writer", Modifier.PRIVATE, Modifier.FINAL)
                    .superclass(ParameterizedTypeName.get(ClassName.get(Meta.class), queueMeta.classType))
                    .addMethod(generateConstructor())
                    .addMethod(generateListToDataClassMethod())
                    .build();
        }

        private MethodSpec generateConstructor() {
            return MethodSpec
                    .constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ObjectReader.class, "reader", Modifier.FINAL)
                    .addParameter(ObjectWriter.class, "writer", Modifier.FINAL)
                    .addStatement("this.$N = $N", "reader", "reader")
                    .addStatement("this.$N = $N", "writer", "writer")
                    .build();
        }

        private MethodSpec generateListToDataClassMethod() {
            return MethodSpec.methodBuilder("fromList")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(wildCardList, "values", Modifier.FINAL)
                    .returns(ParameterizedTypeName.get(ClassName.get(TaskInfo.class), queueMeta.classType))
                    .beginControlFlow("try")
                    .addStatement("$T id = (($T) values.get(0)).longValue()", long.class, Number.class)
                    .addStatement("$T taskStatus = $T.getBySymbol(($T) values.get(1))", TaskStatus.class, TaskStatus.class, String.class)
                    .addStatement("$T task = reader.readValue(($T) values.get(2))", queueMeta.classType, String.class)
                    .addStatement("return new $T<>(id, taskStatus, task)", TaskInfo.class)
                    .nextControlFlow("catch($T e)", Exception.class)
                    .addStatement("throw new $T(e)", RuntimeException.class)
                    .endControlFlow()
                    .build();
        }
    }
}