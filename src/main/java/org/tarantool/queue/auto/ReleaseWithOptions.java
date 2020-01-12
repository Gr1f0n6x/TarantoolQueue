package org.tarantool.queue.auto;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import org.tarantool.queue.internals.operations.EvalOperation;

import javax.lang.model.element.Modifier;

interface ReleaseWithOptions {
    default MethodSpec generateReleaseWithOptions(TypeName builderType) {
        return MethodSpec
                .methodBuilder("releaseWithOptions")
                .addModifiers(Modifier.PUBLIC)
                .returns(builderType)
                .addParameter(long.class, "taskId", Modifier.FINAL)
                .addStatement("return new ReleaseOperationBuilder(tarantoolClient, meta, taskId)", EvalOperation.class, String.class)
                .build();
    }
}
