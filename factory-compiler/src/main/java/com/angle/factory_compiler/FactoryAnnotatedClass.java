package com.angle.factory_compiler;

import com.angle.annotation.Factory;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

/**
 * 我们可以将annotatedElement中包含的信息封装成一个对象，方便后续使用，
 * 因此，另外可以另外声明一个FactoryAnnotatedClass来解析并存放annotatedElement的相关信息。
 */
public class FactoryAnnotatedClass {
    private TypeElement mAnnotatedClassElement;
    private String mQualifiedSuperClassName;
    private String mSimpleTypeName;
    private String mId;

    public FactoryAnnotatedClass(TypeElement classElement) {
        this.mAnnotatedClassElement = classElement;
        Factory annotation = classElement.getAnnotation(Factory.class);
        mId = annotation.id();
        if (mId.length() == 0) {
            throw new IllegalArgumentException(
                    String.format("id() in @%s for class %s is null or empty! that's not allowed",
                            Factory.class.getSimpleName(), classElement.getQualifiedName().toString()));
        }

        // Get the full QualifiedTypeName
        try {  // 该类已经被编译
            Class<?> clazz = annotation.type();
            //这里获取的应该是全路径
            mQualifiedSuperClassName = clazz.getCanonicalName();
            //这里获取的是类的名称
            mSimpleTypeName = clazz.getSimpleName();
        } catch (MirroredTypeException mte) {
            // 该类未被编译
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            // TODO: 2019-09-11 查查这两个类的含义
            mQualifiedSuperClassName = classTypeElement.getQualifiedName().toString();
            mSimpleTypeName = classTypeElement.getSimpleName().toString();
        }
    }

    public TypeElement getAnnotatedClassElement() {
        return mAnnotatedClassElement;
    }

    public String getQualifiedSuperClassName() {
        return mQualifiedSuperClassName;
    }

    public String getSimpleTypeName() {
        return mSimpleTypeName;
    }

    public String getId() {
        return mId;
    }
}
