package com.angle.factory_compiler;

import com.angle.annotation.Factory;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * 参考文章:
 * https://blog.csdn.net/qq_20521573/article/details/82321755
 * <p>
 * 以下这些内容是用来apt写文件所注意的几个类
 * PackageElement 表示一个包程序元素。提供对有关包及其成员的信息的访问。
 * ExecutableElement 表示某个类或接口的方法、构造方法或初始化程序（静态或实例），包括注释类型元素。
 * TypeElement 表示一个类或接口程序元素。提供对有关类型及其成员的信息的访问。注意，枚举类型是一种类，而注解类型是一种接口。
 * VariableElement 表示一个字段、enum 常量、方法或构造方法参数、局部变量或异常参数。
 * <p>
 * 可以参考一下内容
 * <p>
 * package com.zhpan.mannotation.factory;  //    PackageElement
 * <p>
 * public class Circle {  //  TypeElement
 * <p>
 * private int i; //   VariableElement
 * private Triangle triangle;  //  VariableElement
 * <p>
 * public Circle() {} //    ExecuteableElement
 * <p>
 * public void draw(   //  ExecuteableElement
 * String s)   //  VariableElement
 * {
 * System.out.println(s);
 * }
 *
 * @Override public void draw() {    //  ExecuteableElement
 * System.out.println("Draw a circle");
 * }
 * }
 */
public class FactoryProcessor extends AbstractProcessor {

    /**
     * 类型工具
     */
    private Types mTypeUtils;
    /**
     * 错误信息
     */
    private Messager mMessager;
    /**
     * 编写新文件
     */
    private Filer mFiler;
    /**
     * 元素
     */
    private Elements mElementUtils;

    private Map<String, FactoryGroupedClasses> mFactoryClasses = new LinkedHashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        /*
         * 这个方法用于初始化处理器，
         * 方法中有一个ProcessingEnvironment类型的参数，
         * ProcessingEnvironment是一个注解处理工具的集合。
         * 它包含了众多工具类。
         * 例如：Filer可以用来编写新文件；
         * Message可以用来打印错误信息；
         * Elements是一个可以处理Element的工具类。
         */
        mTypeUtils = processingEnvironment.getTypeUtils();
        mMessager = processingEnvironment.getMessager();
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();

    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        /*
         * 这个方法的返回值是一个Set集合，集合中指要处理的注解类型的名称(这里必须是完整的包名+类名，
         * 例如com.example.annotation.Factory)。
         * 由于在本例中只需要处理@Factory注解，
         * 因此Set集合中只需要添加@Factory的名称即可。
         */
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Factory.class.getCanonicalName());
        return annotations;
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
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        /**
         * 先看这个方法的返回值，是一个boolean类型，返回值表示注解是否由当前Processor 处理。
         * 如果返回 true，则这些注解由此注解来处理，后续其它的 Processor 无需再处理它们；
         * 如果返回 false，则这些注解未在此Processor中处理并，那么后续 Processor 可以继续处理它们。
         * 在这个方法的方法体中，我们可以校验被注解的对象是否合法、可以编写处理注解的代码，以及自动生成需要的java文件等。
         * 因此说这个方法是AbstractProcessor 中的最重要的一个方法。我们要处理的大部分逻辑都是在这个方法中完成。
         */

        // TODO: 2019-09-11 查看一下Element是个什么东西
        try {
            for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(Factory.class)) {
                // 正常情况下，这个集合中应该包含的是所有被Factory注解的Shape类的元素，也就是一个TypeElement。
                // 但在编写程序代码时可能有新来的同事不太了解@Factory的用途而误把@Factory用在接口或者抽象类上，这是不符合我们的标准的。
                // 因此，需要在process方法中判断被@Factory注解的元素是否是一个类，如果不是一个类元素，那么就抛出异常，终止编译。

                if (annotatedElement.getKind() != ElementKind.CLASS) {
                    throw new RuntimeException("不可以在抽象类和接口上注册");
                }


                TypeElement typeElement = (TypeElement) annotatedElement;
                //这里根据面向对象思想,所以这里应该封装成一个对象
                FactoryAnnotatedClass annotatedClass = new FactoryAnnotatedClass(typeElement);

                checkValidClass(annotatedClass);

                // Everything is fine, so try to add
                FactoryGroupedClasses factoryClass = mFactoryClasses.get(annotatedClass.getQualifiedSuperClassName());
                if (factoryClass == null) {
                    String qualifiedGroupName = annotatedClass.getQualifiedSuperClassName();
                    factoryClass = new FactoryGroupedClasses(qualifiedGroupName);
                    mFactoryClasses.put(qualifiedGroupName, factoryClass);
                }

                // Checks if id is conflicting with another @Factory annotated class with the same id
                factoryClass.add(annotatedClass);
            }

            // Generate code
            for (FactoryGroupedClasses factoryClass : mFactoryClasses.values()) {
                factoryClass.generateCode(mElementUtils, mFiler);
            }
            mFactoryClasses.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private void checkValidClass(FactoryAnnotatedClass item) {
        /*
         * 为了生成合乎要求的ShapeFactory类，在生成ShapeFactory代码前需要对被Factory注解的元素进行一系列的校验，
         * 只有通过校验，符合要求了才可以生成ShapeFactory代码。
         * 根据需求，我们列出如下规则：
         * 1.只有类才能被@Factory注解。因为在ShapeFactory中我们需要实例化Shape对象，虽然@Factory注解声明了Target为ElementType.TYPE，但接口和枚举并不符合我们的要求。
         * 2.被@Factory注解的类中需要有public的构造方法，这样才能实例化对象。
         * 3.被注解的类必须是type指定的类的子类
         * 4.id需要为String类型，并且需要在相同type组中唯一
         * 5.具有相同type的注解类会被生成在同一个工厂类中
         */

        //获取相应的TypeElement
        // TODO: 2019-09-11 看看这个TypeElement是个什么东西
        TypeElement classElement = item.getAnnotatedClassElement();

        // TODO: 2019-09-11  getModifiers() 方法的含义
        if (!classElement.getModifiers().contains(Modifier.PUBLIC)) {
            //如果不是public的类就抛异常
            throw new RuntimeException("The class is not public.");
        }

        if (classElement.getModifiers().contains(Modifier.ABSTRACT)) {
            // 如果是抽象方法则抛出异常终止编译
            throw new RuntimeException("The class is abstract.");
        }

        // 这个类必须是在@Factory.type()中指定的类的子类，否则抛出异常终止编译
        // TODO: 2019-09-11 看看 getQualifiedSuperClassName()方法的含义
        TypeElement superClassElement = mElementUtils.getTypeElement(item.getQualifiedSuperClassName());
        if (superClassElement.getKind() == ElementKind.INTERFACE) {
            // 检查被注解类是否实现或继承了@Factory.type()所指定的类型，此处均为IShape
            if (!classElement.getInterfaces().contains(superClassElement.asType())) {
                throw new RuntimeException("The class annotated with  must implement the interface");
            }
        } else {
            TypeElement currentClass = classElement;

            while (true) {
                TypeMirror superClassType = currentClass.getSuperclass();

                if (superClassType.getKind() == TypeKind.NONE) {
                    // 向上遍历父类，直到Object也没获取到所需父类，终止编译抛出异常
                    throw new RuntimeException("The class annotated with must inherit from");
                }

                if (superClassType.toString().equals(item.getQualifiedSuperClassName())) {
                    // 校验通过，终止遍历
                    break;
                }

                // TODO: 2019-09-11 这个是干什么的
                currentClass = (TypeElement) mTypeUtils.asElement(superClassType);
            }

            // 检查是否由public的无参构造方法
            for (Element enclosed : classElement.getEnclosedElements()) {
                if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                    ExecutableElement constructorElement = (ExecutableElement) enclosed;
                    if (constructorElement.getParameters().size() == 0 &&
                            constructorElement.getModifiers().contains(Modifier.PUBLIC)) {
                        // 存在public的无参构造方法，检查结束
                        return;
                    }
                }
            }

            // 为检测到public的无参构造方法，抛出异常，终止编译
            throw new RuntimeException("The class must provide an public empty default constructor");
        }
    }
}
