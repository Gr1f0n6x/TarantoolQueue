package org.tarantool.queue.auto;

import com.squareup.javapoet.*;
import org.tarantool.TarantoolClient;
import org.tarantool.queue.internals.Meta;
import org.tarantool.queue.internals.operations.EvalOperation;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import static org.tarantool.queue.auto.BuilderUtils.*;

final class UtubeQueueManagerGenerator extends QueueManagerGenerator implements PutWithOptions, ReleaseWithOptions {
    public UtubeQueueManagerGenerator(Filer filer, QueueMeta queueMeta) {
        super(filer, queueMeta);
    }

    @Override
    protected TypeSpec.Builder queueBuilder(TypeSpec metaSpec) {
        PutOperationBuilderGenerator putOperationBuilderGenerator = new PutOperationBuilderGenerator(operationResultType, queueMeta);
        ReleaseOperationBuilderGenerator releaseOperationBuilderGenerator = new ReleaseOperationBuilderGenerator(operationResultType, queueMeta);

        TypeName putBuilderTypeName = ClassName.get(Common.PACKAGE_NAME, queueMeta.taskManagerName + ".PutOperationBuilder");
        TypeName releaseBuilderTypeName = ClassName.get(Common.PACKAGE_NAME, queueMeta.taskManagerName + ".ReleaseOperationBuilder");

        TypeSpec.Builder builder = super.queueBuilder(metaSpec);
        builder
                .addType(putOperationBuilderGenerator.generate())
                .addType(releaseOperationBuilderGenerator.generate())
                .addMethod(generatePutWithOptions(putBuilderTypeName, queueMeta))
                .addMethod(generateReleaseWithOptions(releaseBuilderTypeName));

        return builder;
    }

    static class PutOperationBuilderGenerator implements Utube {
        private final QueueMeta queueMeta;
        private final TypeName operationResultType;

        public PutOperationBuilderGenerator(TypeName operationResultType, QueueMeta queueMeta) {
            this.queueMeta = queueMeta;
            this.operationResultType = operationResultType;
        }

        public TypeSpec generate() {
            TypeName typeName = ClassName.get(Common.PACKAGE_NAME, queueMeta.taskManagerName + ".PutOperationBuilder");

            return TypeSpec
                    .classBuilder("PutOperationBuilder")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addField(String.class, "utube", Modifier.PRIVATE)
                    .addField(String.class, "taskJson", Modifier.PRIVATE, Modifier.FINAL)
                    .addField(TarantoolClient.class, "tarantoolClient", Modifier.PRIVATE, Modifier.FINAL)
                    .addField(ParameterizedTypeName.get(ClassName.get(Meta.class), queueMeta.classType), "meta", Modifier.PRIVATE, Modifier.FINAL)
                    .addMethod(generateConstructor())
                    .addMethod(utube(typeName))
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
                    .beginControlFlow("if (utube != null)")
                    .addStatement("options.append(\"$L=\").append(\"'\").append($L).append(\"'\").append(\",\")", "utube", "utube")
                    .endControlFlow()
                    .addStatement("return new $T<>(tarantoolClient, meta, $T.format($S, queueName, taskJson, options.toString()))", EvalOperation.class, String.class, "return queue.tube.%s:put('%s', {%s})")
                    .build();
        }
    }

    static class ReleaseOperationBuilderGenerator implements Utube {
        private final QueueMeta queueMeta;
        private final TypeName operationResultType;

        public ReleaseOperationBuilderGenerator(TypeName operationResultType, QueueMeta queueMeta) {
            this.queueMeta = queueMeta;
            this.operationResultType = operationResultType;
        }

        public TypeSpec generate() {
            TypeName typeName = ClassName.get(Common.PACKAGE_NAME, queueMeta.taskManagerName + ".ReleaseOperationBuilder");

            return TypeSpec
                    .classBuilder("ReleaseOperationBuilder")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addField(String.class, "utube", Modifier.PRIVATE)
                    .addField(long.class, "taskId", Modifier.PRIVATE, Modifier.FINAL)
                    .addField(TarantoolClient.class, "tarantoolClient", Modifier.PRIVATE, Modifier.FINAL)
                    .addField(ParameterizedTypeName.get(ClassName.get(Meta.class), queueMeta.classType), "meta", Modifier.PRIVATE, Modifier.FINAL)
                    .addMethod(generateConstructor())
                    .addMethod(utube(typeName))
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
                    .beginControlFlow("if (utube != null)")
                    .addStatement("options.append(\"$L=\").append(\"'\").append($L).append(\"'\").append(\",\")", "utube", "utube")
                    .endControlFlow()
                    .addStatement("return new $T<>(tarantoolClient, meta, $T.format($S, queueName, taskId, options.toString()))", EvalOperation.class, String.class, "return queue.tube.%s:release(%s, {%s})")
                    .build();
        }
    }
}
