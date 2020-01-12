package org.tarantool.queue.auto;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Modifier;

abstract class BuilderUtils {
    public interface Ttl {
        default MethodSpec ttl(TypeName typeName) {
            return MethodSpec
                    .methodBuilder("setTtl")
                    .returns(typeName)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(long.class, "ttl", Modifier.FINAL)
                    .beginControlFlow("if (ttl < 0)")
                    .addStatement("throw new $T($S)", RuntimeException.class, "ttl must be >= 0")
                    .endControlFlow()
                    .addStatement("this.$N = $L", "ttl", "ttl")
                    .addStatement("return this")
                    .build();
        }
    }

    public interface Ttr {
        default MethodSpec ttr(TypeName typeName) {
            return MethodSpec
                    .methodBuilder("setTtr")
                    .returns(typeName)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(long.class, "ttr", Modifier.FINAL)
                    .beginControlFlow("if (ttr < 0)")
                    .addStatement("throw new $T($S)", RuntimeException.class, "ttr must be >= 0")
                    .endControlFlow()
                    .addStatement("this.$N = $L", "ttr", "ttr")
                    .addStatement("return this")
                    .build();
        }
    }

    public interface Priority {
        default MethodSpec priority(TypeName typeName) {
            return MethodSpec
                    .methodBuilder("setPriority")
                    .returns(typeName)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(long.class, "priority", Modifier.FINAL)
                    .beginControlFlow("if (priority < 0)")
                    .addStatement("throw new $T($S)", RuntimeException.class, "priority must be >= 0")
                    .endControlFlow()
                    .addStatement("this.$N = $L", "priority", "priority")
                    .addStatement("return this")
                    .build();
        }
    }

    public interface Timeout {
        default MethodSpec timeout(TypeName typeName) {
            return MethodSpec
                    .methodBuilder("setTimeout")
                    .returns(typeName)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(long.class, "timeout", Modifier.FINAL)
                    .beginControlFlow("if (timeout < 0)")
                    .addStatement("throw new $T($S)", RuntimeException.class, "timeout must be >= 0")
                    .endControlFlow()
                    .addStatement("this.$N = $L", "timeout", "timeout")
                    .addStatement("return this")
                    .build();
        }
    }

    public interface Utube {
        default MethodSpec utube(TypeName typeName) {
            return MethodSpec
                    .methodBuilder("setUtube")
                    .returns(typeName)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(String.class, "utube", Modifier.FINAL)
                    .beginControlFlow("if (utube == null || utube.isEmpty())")
                    .addStatement("throw new $T($S)", RuntimeException.class, "utube name must not be null or empty")
                    .endControlFlow()
                    .addStatement("this.$N = $L", "utube", "utube")
                    .addStatement("return this")
                    .build();
        }
    }

    public interface Delay {
        default MethodSpec delay(TypeName typeName) {
            return MethodSpec
                    .methodBuilder("setDelay")
                    .returns(typeName)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(long.class, "delay", Modifier.FINAL)
                    .beginControlFlow("if (delay < 0)")
                    .addStatement("throw new $T($S)", RuntimeException.class, "delay must be >= 0")
                    .endControlFlow()
                    .addStatement("this.$N = $L", "delay", "delay")
                    .addStatement("return this")
                    .build();
        }
    }
}
