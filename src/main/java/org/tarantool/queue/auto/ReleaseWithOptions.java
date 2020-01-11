package org.tarantool.queue.auto;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import org.tarantool.queue.internals.operations.EvalOperation;

import javax.lang.model.element.Modifier;

interface ReleaseWithOptions {
    default MethodSpec generateReleaseWithOptions(Class<?> builderType, QueueMeta queueMeta) {
        return MethodSpec
                .methodBuilder("releaseWithOptions")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(builderType), queueMeta.classType))
                .addParameter(long.class, "taskId", Modifier.FINAL)
                .addStatement("return new ReleaseOperationBuilder(tarantoolClient, meta, taskId)", EvalOperation.class, String.class)
                .build();
    }
}
