package org.tarantool.queue.auto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.squareup.javapoet.*;
import org.tarantool.TarantoolClient;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

final class TaskManagerFactoryGenerator {
    private final Filer filer;

    public TaskManagerFactoryGenerator(Filer filer) {
        this.filer = filer;
    }

    public void generate(List<TaskMeta> metaList) throws IOException {
        TypeSpec factory = TypeSpec
                .classBuilder("TaskManagerFactory")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(TarantoolClient.class, "tarantoolClient", Modifier.PRIVATE, Modifier.FINAL)
                .addField(ObjectMapper.class, "objectMapper", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(generatePrimaryConstructor())
                .addMethod(generateSecondaryConstructor())
                .addMethods(generateFactories(metaList))
                .build();

        JavaFile javaFile = JavaFile.builder(Common.PACKAGE_NAME, factory)
                .build();

        javaFile.writeTo(filer);
    }

    private MethodSpec generatePrimaryConstructor() {
        return MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TarantoolClient.class, "tarantoolClient", Modifier.FINAL)
                .addCode("this.$N = $N", "tarantoolClient", "tarantoolClient")
                .addCode("this.$N = new $T()", "objectMapper", ObjectMapper.class)
                .build();
    }

    private MethodSpec generateSecondaryConstructor() {
        return MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TarantoolClient.class, "tarantoolClient", Modifier.FINAL)
                .addParameter(ObjectMapper.class, "objectMapper", Modifier.FINAL)
                .addCode("this.$N = $N", "tarantoolClient", "tarantoolClient")
                .addCode("this.$N = $N", "objectMapper", "objectMapper")
                .build();
    }

    private Iterable<MethodSpec> generateFactories(List<TaskMeta> metas) {
        return metas
                .stream()
                .map(meta -> {
                    ClassName generatedClass = ClassName.get(Common.PACKAGE_NAME, meta.taskManagerName);

                    return MethodSpec
                            .methodBuilder(Common.capitalize(meta.taskManagerName))
                            .addModifiers(Modifier.PUBLIC)
                            .returns(generatedClass)
                            .addStatement("$T reader = $N.reader()", ObjectReader.class, "objectMapper")
                            .addStatement("$T writer = $N.writer()", ObjectWriter.class, "objectMapper")
                            .addStatement("return new $T($N, reader, writer)", generatedClass, "tarantoolClient")
                            .build();
                })
                .collect(Collectors.toList());
    }
}
