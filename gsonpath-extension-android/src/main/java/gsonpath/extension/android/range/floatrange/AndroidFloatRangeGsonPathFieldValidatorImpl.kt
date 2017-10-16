package gsonpath.extension.android.range.floatrange

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

/**
 * A {@link GsonPathExtension} that supports the '@FloatRange' Android Support Library annotation.
 */
open class AndroidFloatRangeGsonPathFieldValidatorImpl : GsonPathExtension {
    private val BOXED_FLOAT = ClassName.get("java.lang", "Float")
    private val BOXED_DOUBLE = ClassName.get("java.lang", "Double")

    override fun getExtensionName(): String {
        return "Android Support Library 'FloatRange' Annotation"
    }

    override fun createFieldReadCodeBlock(processingEnv: ProcessingEnvironment, fieldInfo: FieldInfo,
                                          variableName: String): CodeBlock? {

        val floatRangeAnnotation: AnnotationMirror = getAnnotationMirror(fieldInfo.element,
                "android.support.annotation", "FloatRange") ?: return null

        // Ensure that the field is either a float, or a double.
        val typeName =
                if (fieldInfo.typeName.isPrimitive) {
                    fieldInfo.typeName.box()
                } else {
                    fieldInfo.typeName
                }

        if (typeName != BOXED_DOUBLE && typeName != BOXED_FLOAT) {
            throw ProcessingException("Unexpected type found for field annotated with 'FloatRange', only " +
                    "floats and doubles are allowed.", fieldInfo.element)
        }

        val fieldName = fieldInfo.fieldName
        val validationBuilder = CodeBlock.builder()
                .handleFrom(floatRangeAnnotation, fieldName, variableName)
                .handleTo(floatRangeAnnotation, fieldName, variableName)

        val validationCodeBlock = validationBuilder.build()
        if (!validationCodeBlock.isEmpty) {
            return validationCodeBlock
        }
        return null
    }

    /**
     * Adds the range 'from' validation if the fromValue does not equal the floor-value.
     *
     * @param floatRangeAnnotationMirror the annotation to obtain the range values
     * @param fieldName the name of the field being validated
     * @param variableName the name of the variable that is assigned back to the fieldName
     */
    private fun CodeBlock.Builder.handleFrom(floatRangeAnnotationMirror: AnnotationMirror, fieldName: String,
                                             variableName: String): CodeBlock.Builder {

        val fromValue: Double = getAnnotationValueObject(floatRangeAnnotationMirror, "from") as Double? ?: return this
        val fromInclusive: Boolean = getAnnotationValueObject(floatRangeAnnotationMirror, "fromInclusive") as Boolean? ?: true

        if (fromValue == Double.NEGATIVE_INFINITY) {
            return this
        }

        return handleRangeValue(fromValue.toString(), true, fromInclusive, fieldName, variableName)
    }

    /**
     * Adds the range 'to' validation if the toValue does not equal the ceiling-value.
     *
     * @param floatRangeAnnotationMirror the annotation to obtain the range values
     * @param fieldName the name of the field being validated
     * @param variableName the name of the variable that is assigned back to the fieldName
     */
    private fun CodeBlock.Builder.handleTo(floatRangeAnnotationMirror: AnnotationMirror, fieldName: String,
                                           variableName: String): CodeBlock.Builder {

        val toValue: Double = getAnnotationValueObject(floatRangeAnnotationMirror, "to") as Double? ?: return this
        val toInclusive: Boolean = getAnnotationValueObject(floatRangeAnnotationMirror, "toInclusive") as Boolean? ?: true

        if (toValue == java.lang.Double.POSITIVE_INFINITY) {
            return this
        }

        return handleRangeValue(toValue.toString(), false, toInclusive, fieldName, variableName)
    }
}
