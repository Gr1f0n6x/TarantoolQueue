package org.tarantool.queue.auto;

import com.google.auto.service.AutoService;
import org.tarantool.queue.annotations.Queue;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("org.tarantool.queue.annotations.Queue")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public final class QueueProcessor extends AbstractProcessor {
    private Filer filer;
    private Messager messager;
    private Types typeUtils;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            List<QueueMeta> metaList = generateTaskManagers(roundEnv);
            generateTaskManagerFactory(metaList);
        } catch (Throwable e) {
            error(e.getLocalizedMessage());
            return true;
        }

        return false;
    }

    private List<QueueMeta> generateTaskManagers(RoundEnvironment roundEnv) throws IOException {
        List<QueueMeta> metaList = new ArrayList<>();

        for (Element element : roundEnv.getElementsAnnotatedWith(Queue.class)) {
            if (element.getKind() != ElementKind.CLASS && element.getKind() != ElementKind.INTERFACE) {
                throw new IllegalArgumentException("Only classes and interfaces may be annotated by Queue");
            }

            QueueMeta queueMeta = QueueMeta.getInstance((TypeElement) element);
            QueueManagerGenerator.getInstance(filer, queueMeta).generate();
            metaList.add(queueMeta);
        }

        return metaList;
    }

    private void generateTaskManagerFactory(List<QueueMeta> metas) throws IOException {
        QueueManagerFactoryGenerator generator = new QueueManagerFactoryGenerator(filer);

        if (!metas.isEmpty()) {
            generator.generate(metas);
        }
    }

    private void error(String message, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(message, args));
    }
}
