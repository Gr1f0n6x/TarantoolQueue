package org.tarantool.queue.auto;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

final class FifoTtlQueueManagerGenerator extends TtlQueueManagerGenerator {
    public FifoTtlQueueManagerGenerator(Filer filer, QueueMeta queueMeta) {
        super(filer, queueMeta);
    }

    @Override
    protected TypeSpec.Builder queueBuilder(TypeName operationResultType, TypeSpec metaSpec) {
        return super.queueBuilder(operationResultType, metaSpec);
    }
}