package org.tarantool.queue.auto;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;

final class LimFifoTtlQueueManagerGenerator extends TtlQueueManagerGenerator {
    public LimFifoTtlQueueManagerGenerator(Filer filer, QueueMeta queueMeta) {
        super(filer, queueMeta);
    }

    @Override
    protected TypeSpec.Builder queueBuilder(TypeName operationResultType, TypeSpec metaSpec) {
        return super.queueBuilder(operationResultType, metaSpec);
    }
}
