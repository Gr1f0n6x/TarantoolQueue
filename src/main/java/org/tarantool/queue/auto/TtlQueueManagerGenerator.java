package org.tarantool.queue.auto;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.tarantool.queue.internals.operations.EvalOperation;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

abstract class TtlQueueManagerGenerator extends QueueManagerGenerator {
    public TtlQueueManagerGenerator(Filer filer, QueueMeta queueMeta) {
        super(filer, queueMeta);
    }

    @Override
    protected TypeSpec.Builder queueBuilder(TypeSpec metaSpec) {
        return super.queueBuilder(metaSpec)
                .addMethod(generateTouch());
    }

    private MethodSpec generateTouch() {
        return MethodSpec
                .methodBuilder("touch")
                .addModifiers(Modifier.PUBLIC)
                .returns(operationResultType)
                .addParameter(long.class, "taskId", Modifier.FINAL)
                .addParameter(long.class, "increment", Modifier.FINAL)
                .beginControlFlow("if (increment < 0)")
                .addStatement("throws new $T($S)", IllegalArgumentException.class, "increment should be >= 0")
                .endControlFlow()
                .addStatement("return new $T<>(tarantoolClient, meta, $T.format($S, queueName, taskId, increment))", EvalOperation.class, String.class, "return queue.tube.%s:touch(%s, %s)")
                .build();
    }
}
