package gsonpath.extension.android

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element

fun getAnnotationMirror(element: Element, packageName: String, className: String): AnnotationMirror? {
    return element.annotationMirrors.find {
        ClassName.get(it.annotationType) == ClassName.get(packageName, className)
    } ?: return null
}

fun getAnnotationValue(annotationMirror: AnnotationMirror, propertyName: String): AnnotationValue? {
    return annotationMirror.elementValues.toList().find {
        it.first.simpleName.toString() == propertyName
    }?.second
}

fun getAnnotationValueObject(annotationMirror: AnnotationMirror, propertyName: String): Any? {
    return getAnnotationValue(annotationMirror, propertyName)?.value
}

fun CodeBlock.Builder.addException(exceptionText: String): CodeBlock.Builder {
    return this.addStatement("""throw new com.google.gson.JsonParseException("$exceptionText")""")
}