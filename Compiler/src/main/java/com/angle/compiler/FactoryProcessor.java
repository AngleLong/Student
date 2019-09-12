package com.angle.compiler;

import com.google.auto.service.AutoService;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

/**
 * PackageElement 表示一个包程序元素。提供对有关包及其成员的信息的访问。
 * ExecutableElement 表示某个类或接口的方法、构造方法或初始化程序（静态或实例），包括注释类型元素。
 * TypeElement 表示一个类或接口程序元素。提供对有关类型及其成员的信息的访问。注意，枚举类型是一种类，而注解类型是一种接口。
 * VariableElement 表示一个字段、enum 常量、方法或构造方法参数、局部变量或异常参数。
 */
@AutoService(Processor.class)
public class FactoryProcessor extends AbstractProcessor {
    /**
     * 这个相当于上面得四种类型
     */
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        /*
         * 这个方法用于初始化处理器，
         * 方法中有一个ProcessingEnvironment类型的参数，ProcessingEnvironment是一个注解处理工具的集合。
         * 它包含了众多工具类。例如：
         * Filer可以用来编写新文件；
         * Messager可以用来打印错误信息；
         * Elements是一个可以处理Element的工具类。
         * 原文链接：https://blog.csdn.net/qq_20521573/article/details/82321755
         */

        typeUtils = processingEnvironment.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        /**
         * 先看这个方法的返回值，是一个boolean类型，返回值表示注解是否由当前Processor 处理。
         * 如果返回 true，则这些注解由此注解来处理，后续其它的 Processor 无需再处理它们；
         * 如果返回 false，则这些注解未在此Processor中处理并，那么后续 Processor 可以继续处理它们。
         * 在这个方法的方法体中，我们可以校验被注解的对象是否合法、可以编写处理注解的代码，以及自动生成需要的java文件等。
         * 因此说这个方法是AbstractProcessor 中的最重要的一个方法。我们要处理的大部分逻辑都是在这个方法中完成。
         * 原文链接：https://blog.csdn.net/qq_20521573/article/details/82321755
         */
        return false;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        /*
         * 这个方法非常简单，只有一个返回值，用来指定当前正在使用的Java版本，
         * 通常return SourceVersion.latestSupported()即可。
         */
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        /*
         * 这个方法的返回值是一个Set集合，集合中指要处理的注解类型的名称(这里必须是完整的包名+类名，
         * 例如com.example.annotation.Factory)。
         * 由于在本例中只需要处理@Factory注解，
         * 因此Set集合中只需要添加@Factory的名称即可。
         */
        return super.getSupportedAnnotationTypes();
    }
}
