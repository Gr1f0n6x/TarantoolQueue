package org.tarantool.queue.auto;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import org.tarantool.queue.internals.operations.EvalOperation;

import javax.lang.model.element.Modifier;

interface PutWithOptions {
    default MethodSpec generatePutWithOptions(TypeName builderType, QueueMeta queueMeta) {
        return MethodSpec
                .methodBuilder("putWithOptions")
                .addModifiers(Modifier.PUBLIC)
                .returns(builderType)
                .addParameter(queueMeta.classType, "task", Modifier.FINAL)
                .beginControlFlow("try")
                .addStatement("$T taskJson = writer.writeValueAsString(task)", String.class)
                .addStatement("return new PutOperationBuilder(tarantoolClient, meta, taskJson)", EvalOperation.class, String.class)
                .nextControlFlow("catch ($T e)", Exception.class)
                .addStatement("throw new $T(e)", RuntimeException.class)
                .endControlFlow()
                .build();
    }
}
