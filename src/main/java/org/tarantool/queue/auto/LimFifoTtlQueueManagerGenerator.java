package org.tarantool.queue.auto;

import com.squareup.javapoet.*;
import org.tarantool.TarantoolClient;
import org.tarantool.queue.internals.Meta;
import org.tarantool.queue.internals.builders.LimitBuilder;
import org.tarantool.queue.internals.operations.EvalOperation;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import static org.tarantool.queue.auto.BuilderUtils.*;

final class LimFifoTtlQueueManagerGenerator extends TtlQueueManagerGenerator implements PutWithOptions, ReleaseWithOptions {
    public LimFifoTtlQueueManagerGenerator(Filer filer, QueueMeta queueMeta) {
        super(filer, queueMeta);
    }

    @Override
    protected TypeSpec.Builder queueBuilder(TypeSpec metaSpec) {
        PutOperationBuilderGenerator putOperationBuilderGenerator = new PutOperationBuilderGenerator(operationResultType, queueMeta);
        ReleaseOperationBuilderGenerator releaseOperationBuilderGenerator = new ReleaseOperationBuilderGenerator(operationResultType, queueMeta);

        TypeSpec.Builder builder = super.queueBuilder(metaSpec);
        builder
                .addType(putOperationBuilderGenerator.generate())
                .addType(releaseOperationBuilderGenerator.generate())
                .addMethod(generatePutWithOptions(LimitBuilder.class, queueMeta))
                .addMethod(generateReleaseWithOptions(LimitBuilder.class, queueMeta));

        return builder;
    }

    static class PutOperationBuilderGenerator implements Ttl, Ttr, Delay, Priority, Timeout {
        private final QueueMeta queueMeta;
        private final TypeName operationResultType;

        public PutOperationBuilderGenerator(TypeName operationResultType, QueueMeta queueMeta) {
            this.queueMeta = queueMeta;
            this.operationResultType = operationResultType;
        }

        public TypeSpec generate() {
            TypeName typeName = ParameterizedTypeName.get(ClassName.get(LimitBuilder.class), queueMeta.classType);

            return TypeSpec
                    .classBuilder("PutOperationBuilder")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(typeName)
                    .addField(long.class, "ttl", Modifier.PRIVATE)
                    .addField(long.class, "ttr", Modifier.PRIVATE)
                    .addField(long.class, "priority", Modifier.PRIVATE)
                    .addField(long.class, "delay", Modifier.PRIVATE)
                    .addField(long.class, "timeout", Modifier.PRIVATE)
                    .addField(String.class, "taskJson", Modifier.PRIVATE, Modifier.FINAL)
                    .addField(TarantoolClient.class, "tarantoolClient", Modifier.PRIVATE, Modifier.FINAL)
                    .addField(ParameterizedTypeName.get(ClassName.get(Meta.class), queueMeta.classType), "meta", Modifier.PRIVATE, Modifier.FINAL)
                    .addMethod(generateConstructor())
                    .addMethod(ttl(typeName))
                    .addMethod(ttr(typeName))
                    .addMethod(timeout(typeName))
                    .addMethod(priority(typeName))
                    .addMethod(delay(typeName))
                    .addMethod(generateBuild())
                    .build();
        }

        private MethodSpec generateConstructor() {
            return MethodSpec
                    .constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TarantoolClient.class, "tarantoolClient", Modifier.FINAL)
                    .addParameter(ParameterizedTypeName.get(ClassName.get(Meta.class), queueMeta.classType), "meta", Modifier.FINAL)
                    .addParameter(String.class, "taskJson")
                    .addStatement("this.$N = $N", "taskJson", "taskJson")
                    .addStatement("this.$N = $N", "tarantoolClient", "tarantoolClient")
                    .addStatement("this.$N = $N", "meta", "meta")
                    .build();
        }

        private MethodSpec generateBuild() {
            return MethodSpec
                    .methodBuilder("build")
                    .returns(operationResultType)
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("$T options = new $T()", StringBuilder.class, StringBuilder.class)
                    .beginControlFlow("if (ttl > 0)")
                    .addStatement("options.append(\"'$L'=\").append($L).append(\",\")", "ttl", "ttl")
                    .endControlFlow()
                    .beginControlFlow("if (ttr > 0)")
                    .addStatement("options.append(\"'$L'=\").append($L).append(\",\")", "ttr", "ttr")
                    .endControlFlow()
                    .beginControlFlow("if (delay > 0)")
                    .addStatement("options.append(\"'$L'=\").append($L).append(\",\")", "delay", "delay")
                    .endControlFlow()
                    .beginControlFlow("if (priority > 0)")
                    .addStatement("options.append(\"'$L'=\").append($L).append(\",\")", "pri", "priority")
                    .endControlFlow()
                    .beginControlFlow("if (timeout > 0)")
                    .addStatement("options.append(\"'$L'=\").append($L).append(\",\")", "timeout", "timeout")
                    .endControlFlow()
                    .addStatement("return new $T<>(tarantoolClient, meta, $T.format($S, queueName, taskJson, options.toString()))", EvalOperation.class, String.class, "return queue.tube.%s:put('%s', {%s})")
                    .build();
        }
    }

    static class ReleaseOperationBuilderGenerator implements Ttl, Ttr, Delay, Priority, Timeout {
        private final QueueMeta queueMeta;
        private final TypeName operationResultType;

        public ReleaseOperationBuilderGenerator(TypeName operationResultType, QueueMeta queueMeta) {
            this.queueMeta = queueMeta;
            this.operationResultType = operationResultType;
        }

        public TypeSpec generate() {
            TypeName typeName = ParameterizedTypeName.get(ClassName.get(LimitBuilder.class), queueMeta.classType);

            return TypeSpec
                    .classBuilder("ReleaseOperationBuilder")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(typeName)
                    .addField(long.class, "ttl", Modifier.PRIVATE)
                    .addField(long.class, "ttr", Modifier.PRIVATE)
                    .addField(long.class, "priority", Modifier.PRIVATE)
                    .addField(long.class, "delay", Modifier.PRIVATE)
                    .addField(long.class, "timeout", Modifier.PRIVATE)
                    .addField(long.class, "taskId", Modifier.PRIVATE, Modifier.FINAL)
                    .addField(TarantoolClient.class, "tarantoolClient", Modifier.PRIVATE, Modifier.FINAL)
                    .addField(ParameterizedTypeName.get(ClassName.get(Meta.class), queueMeta.classType), "meta", Modifier.PRIVATE, Modifier.FINAL)
                    .addMethod(generateConstructor())
                    .addMethod(ttl(typeName))
                    .addMethod(ttr(typeName))
                    .addMethod(timeout(typeName))
                    .addMethod(priority(typeName))
                    .addMethod(delay(typeName))
                    .addMethod(generateBuild())
                    .build();
        }

        private MethodSpec generateConstructor() {
            return MethodSpec
                    .constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TarantoolClient.class, "tarantoolClient", Modifier.FINAL)
                    .addParameter(ParameterizedTypeName.get(ClassName.get(Meta.class), queueMeta.classType), "meta", Modifier.FINAL)
                    .addParameter(long.class, "taskId")
                    .addStatement("this.$N = $N", "tarantoolClient", "tarantoolClient")
                    .addStatement("this.$N = $N", "meta", "meta")
                    .addStatement("this.$N = $N", "taskId", "taskId")
                    .build();
        }

        private MethodSpec generateBuild() {
            return MethodSpec
                    .methodBuilder("build")
                    .returns(operationResultType)
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("$T options = new $T()", StringBuilder.class, StringBuilder.class)
                    .beginControlFlow("if (ttl > 0)")
                    .addStatement("options.append(\"'$L'=\").append($L).append(\",\")", "ttl", "ttl")
                    .endControlFlow()
                    .beginControlFlow("if (ttr > 0)")
                    .addStatement("options.append(\"'$L'=\").append($L).append(\",\")", "ttr", "ttr")
                    .endControlFlow()
                    .beginControlFlow("if (delay > 0)")
                    .addStatement("options.append(\"'$L'=\").append($L).append(\",\")", "delay", "delay")
                    .endControlFlow()
                    .beginControlFlow("if (priority > 0)")
                    .addStatement("options.append(\"'$L'=\").append($L).append(\",\")", "pri", "priority")
                    .endControlFlow()
                    .beginControlFlow("if (timeout > 0)")
                    .addStatement("options.append(\"'$L'=\").append($L).append(\",\")", "timeout", "timeout")
                    .endControlFlow()
                    .addStatement("return new $T<>(tarantoolClient, meta, $T.format($S, queueName, taskId, options.toString()))", EvalOperation.class, String.class, "return queue.tube.%s:release(%s, {%s})")
                    .build();
        }
    }
}
