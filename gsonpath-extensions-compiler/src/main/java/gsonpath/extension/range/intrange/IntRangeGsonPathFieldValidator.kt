package gsonpath.extension.range.intrange

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import gsonpath.ProcessingException
import gsonpath.compiler.ExtensionFieldMetadata
import gsonpath.compiler.GsonPathExtension
import gsonpath.extension.getAnnotationMirror
import gsonpath.extension.getAnnotationValueObject
import gsonpath.extension.range.handleRangeValue
import gsonpath.util.codeBlock
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror

/**
 * A {@link GsonPathExtension} that supports the '@IntRange' annotation.
 */
class IntRangeGsonPathFieldValidator : GsonPathExtension {
    private val BOXED_INT = ClassName.get("java.lang", "Integer")
    private val BOXED_LONG = ClassName.get("java.lang", "Long")

    override val extensionName: String
        get() = "'IntRange' Annotation"


    override fun createFieldReadCodeBlock(processingEnvironment: ProcessingEnvironment,
                                          extensionFieldMetadata: ExtensionFieldMetadata): CodeBlock? {

        val (fieldInfo, variableName, jsonPath) = extensionFieldMetadata

        val intRangeAnnotation: AnnotationMirror =
                getAnnotationMirror(fieldInfo.element, "android.support.annotation", "IntRange")
                        ?: getAnnotationMirror(fieldInfo.element, "gsonpath.extension.annotation", "IntRange")
                        ?: return null

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

        val validationCodeBlock = codeBlock {
            handleFrom(intRangeAnnotation, jsonPath, variableName)
            handleTo(intRangeAnnotation, jsonPath, variableName)
        }
        if (!validationCodeBlock.isEmpty) {
            return validationCodeBlock
        }
        return null
    }

    /**
     * Adds the range 'from' validation if the fromValue does not equal the floor-value.
     *
     * @param intRangeAnnotationMirror the annotation to obtain the range values
     * @param jsonPath the json path of the field being validated
     * @param variableName the name of the variable that is assigned back to the fieldName
     */
    private fun CodeBlock.Builder.handleFrom(intRangeAnnotationMirror: AnnotationMirror, jsonPath: String,
                                             variableName: String): CodeBlock.Builder {

        val fromValue: Long = getAnnotationValueObject(intRangeAnnotationMirror, "from") as Long? ?: return this

        if (fromValue == java.lang.Long.MIN_VALUE) {
            return this
        }

        return handleRangeValue(fromValue.toString(), true, true, jsonPath, variableName)
    }

    /**
     * Adds the range 'to' validation if the toValue does not equal the ceiling-value.
     *
     * @param intRangeAnnotationMirror the annotation to obtain the range values
     * @param jsonPath the json path of the field being validated
     * @param variableName the name of the variable that is assigned back to the fieldName
     */
    private fun CodeBlock.Builder.handleTo(intRangeAnnotationMirror: AnnotationMirror, jsonPath: String,
                                           variableName: String): CodeBlock.Builder {

        val toValue: Long = getAnnotationValueObject(intRangeAnnotationMirror, "to") as Long? ?: return this

        if (toValue == java.lang.Long.MAX_VALUE) {
            return this
        }

        return handleRangeValue(toValue.toString(), false, true, jsonPath, variableName)
    }
}
