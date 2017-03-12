package gsonpath.extension.android.range.intrange

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import gsonpath.ProcessingException
import gsonpath.compiler.GsonPathExtension
import gsonpath.extension.android.getAnnotationMirror
import gsonpath.extension.android.getAnnotationValueObject
import gsonpath.extension.android.range.handleRangeValue
import gsonpath.model.FieldInfo
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror

open class AndroidIntRangeGsonPathFieldValidatorImpl : GsonPathExtension {
    private val BOXED_INT = ClassName.get("java.lang", "Integer")
    private val BOXED_LONG = ClassName.get("java.lang", "Long")

    override fun getExtensionName(): String {
        return "Android Support Library 'IntRange' Annotation"
    }

    override fun createFieldReadCodeBlock(processingEnv: ProcessingEnvironment, fieldInfo: FieldInfo,
                                       variableName: String): CodeBlock? {

        val element = fieldInfo.element ?: return null

        val intRangeAnnotation: AnnotationMirror = getAnnotationMirror(element,
                "android.support.annotation", "IntRange") ?: return null

        // Ensure that the field is either a integer, or a long.
        val typeName =
                if (fieldInfo.typeName.isPrimitive) {
                    fieldInfo.typeName.box()
                } else {
                    fieldInfo.typeName
                }

        if (typeName != BOXED_INT && typeName != BOXED_LONG) {
            throw ProcessingException("Unexpected type found for field annotated with 'IntRange', only " +
                    "integers and longs are allowed.", fieldInfo.element)
        }

        val fieldName = fieldInfo.fieldName
        val validationBuilder = CodeBlock.builder()
                .handleFrom(intRangeAnnotation, fieldName, variableName)
                .handleTo(intRangeAnnotation, fieldName, variableName)

        val validationCodeBlock = validationBuilder.build()
        if (!validationCodeBlock.isEmpty) {
            return validationCodeBlock
        }
        return null
    }

    private fun CodeBlock.Builder.handleFrom(intRangeAnnotationMirror: AnnotationMirror, fieldName: String,
                                             variableName: String): CodeBlock.Builder {

        val fromValue: Long = getAnnotationValueObject(intRangeAnnotationMirror, "from") as Long? ?: return this

        if (fromValue == java.lang.Long.MIN_VALUE) {
            return this
        }

        return handleRangeValue(fromValue.toString(), true, true, fieldName, variableName)
    }

    private fun CodeBlock.Builder.handleTo(intRangeAnnotationMirror: AnnotationMirror, fieldName: String,
                                           variableName: String): CodeBlock.Builder {

        val toValue: Long = getAnnotationValueObject(intRangeAnnotationMirror, "to") as Long? ?: return this

        if (toValue == java.lang.Long.MAX_VALUE) {
            return this
        }

        return handleRangeValue(toValue.toString(), false, true, fieldName, variableName)
    }
}
