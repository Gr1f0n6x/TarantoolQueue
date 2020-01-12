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

final class QueueManagerFactoryGenerator {
    private final Filer filer;

    public QueueManagerFactoryGenerator(Filer filer) {
        this.filer = filer;
    }

    public void generate(List<QueueMeta> metaList) throws IOException {
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
                .addStatement("this.$N = $N", "tarantoolClient", "tarantoolClient")
                .addStatement("this.$N = new $T()", "objectMapper", ObjectMapper.class)
                .build();
    }

    private MethodSpec generateSecondaryConstructor() {
        return MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TarantoolClient.class, "tarantoolClient", Modifier.FINAL)
                .addParameter(ObjectMapper.class, "objectMapper", Modifier.FINAL)
                .addStatement("this.$N = $N", "tarantoolClient", "tarantoolClient")
                .addStatement("this.$N = $N", "objectMapper", "objectMapper")
                .build();
    }

    private Iterable<MethodSpec> generateFactories(List<QueueMeta> metas) {
        return metas
                .stream()
                .map(meta -> {
                    ClassName generatedClass = ClassName.get(Common.PACKAGE_NAME, meta.taskManagerName);

                    return MethodSpec
                            .methodBuilder(queueManagerFactoryName(meta.taskManagerName))
                            .addModifiers(Modifier.PUBLIC)
                            .returns(generatedClass)
                            .addStatement("$T reader = $N.readerFor($T.class)", ObjectReader.class, "objectMapper", meta.classType)
                            .addStatement("$T writer = $N.writerFor($T.class)", ObjectWriter.class, "objectMapper", meta.classType)
                            .addStatement("return new $T($N, reader, writer)", generatedClass, "tarantoolClient")
                            .build();
                })
                .collect(Collectors.toList());
    }

    private String queueManagerFactoryName(String value) {
        return value.substring(0, 1).toLowerCase() + value.substring(1) + "Manager";
    }
}
