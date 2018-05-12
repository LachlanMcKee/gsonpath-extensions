package gsonpath.extension.def.size

import com.squareup.javapoet.CodeBlock
import gsonpath.ProcessingException
import gsonpath.compiler.GsonPathExtension
import gsonpath.compiler.isFieldCollectionType
import gsonpath.extension.def.addException
import gsonpath.extension.def.getAnnotationMirror
import gsonpath.extension.def.getAnnotationValueObject
import gsonpath.model.FieldInfo
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.type.ArrayType

/**
 * A {@link GsonPathExtension} that supports the '@Size' annotation.
 */
class SizeGsonPathFieldValidator : GsonPathExtension {

    override fun getExtensionName(): String {
        return "'Size' Annotation"
    }

    override fun createFieldReadCodeBlock(processingEnv: ProcessingEnvironment, fieldInfo: FieldInfo,
                                          variableName: String): CodeBlock? {

        val sizeAnnotation: AnnotationMirror =
            getAnnotationMirror(fieldInfo.element, "android.support.annotation", "Size")
                ?: getAnnotationMirror(fieldInfo.element, "gsonpath.extension.annotation", "Size")
                ?: return null

        // Ensure that the field is either an array or a collection.
        val fieldType: FieldType =
                when {
                    (fieldInfo.typeMirror is ArrayType) -> FieldType.ARRAY

                    isFieldCollectionType(processingEnv, fieldInfo.typeMirror) -> FieldType.COLLECTION

                    else ->
                        throw ProcessingException("Unexpected type found for field annotated with 'Size', only " +
                                "an array, or a collection class may be used.", fieldInfo.element)
                }

        val fieldName = fieldInfo.fieldName
        val validationBuilder = CodeBlock.builder()
                .handleExactLength(sizeAnnotation, fieldName, variableName, fieldType)
                .handleMin(sizeAnnotation, fieldName, variableName, fieldType)
                .handleMax(sizeAnnotation, fieldName, variableName, fieldType)
                .handleMultiple(sizeAnnotation, fieldName, variableName, fieldType)

        val validationCodeBlock = validationBuilder.build()
        if (!validationCodeBlock.isEmpty) {
            return validationCodeBlock
        }
        return null
    }

    /**
     * Adds the size 'min value' validation if the minValue does not equal the floor-value.
     *
     * @param sizeAnnotation the annotation to obtain the size values
     * @param fieldName the name of the field being validated
     * @param variableName the name of the variable that is assigned back to the fieldName
     */
    private fun CodeBlock.Builder.handleMin(sizeAnnotation: AnnotationMirror, fieldName: String,
                                            variableName: String, fieldType: FieldType): CodeBlock.Builder {

        val minValue: Long = getAnnotationValueObject(sizeAnnotation, "min") as Long? ?: return this

        if (minValue == java.lang.Long.MIN_VALUE) {
            return this
        }

        val lengthProperty = fieldType.lengthProperty
        return this.beginControlFlow("if ($variableName.$lengthProperty < $minValue)")

                .addSizeException(fieldType, fieldName,
                        """Expected minimum: '$minValue', actual minimum: '" + $variableName.$lengthProperty + "'""")

                .endControlFlow()
    }

    /**
     * Adds the size 'max value' validation if the maxValue does not equal the ceiling-value.
     *
     * @param sizeAnnotation the annotation to obtain the size values
     * @param fieldName the name of the field being validated
     * @param variableName the name of the variable that is assigned back to the fieldName
     */
    private fun CodeBlock.Builder.handleMax(sizeAnnotation: AnnotationMirror, fieldName: String, variableName: String,
                                            fieldType: FieldType): CodeBlock.Builder {

        val maxValue: Long = getAnnotationValueObject(sizeAnnotation, "max") as Long? ?: return this

        if (maxValue == java.lang.Long.MAX_VALUE) {
            return this
        }

        val lengthProperty = fieldType.lengthProperty
        return this.beginControlFlow("if ($variableName.$lengthProperty > $maxValue)", variableName, maxValue)

                .addSizeException(fieldType, fieldName,
                        """Expected maximum: '$maxValue', actual maximum: '" + $variableName.$lengthProperty + "'""")

                .endControlFlow()
    }

    /**
     * Adds the size 'multiple' validation if the multipleValue does not equal 1.
     *
     * 'Multiple' means that the array/collection must have a size that is a multiple of this value.
     *
     * @param sizeAnnotation the annotation to obtain the size values
     * @param fieldName the name of the field being validated
     * @param variableName the name of the variable that is assigned back to the fieldName
     */
    private fun CodeBlock.Builder.handleMultiple(sizeAnnotation: AnnotationMirror, fieldName: String,
                                                 variableName: String, fieldType: FieldType): CodeBlock.Builder {

        val multipleValue: Long = getAnnotationValueObject(sizeAnnotation, "multiple") as Long? ?: return this

        if (multipleValue == 1L) {
            return this
        }

        val lengthProperty = fieldType.lengthProperty
        return this.beginControlFlow("if ($variableName.$lengthProperty % $multipleValue != 0)", variableName, multipleValue)

                .addSizeException(fieldType, fieldName,
                        """$lengthProperty of '" + $variableName.$lengthProperty + "' is not a multiple of $multipleValue""")

                .endControlFlow()
    }

    /**
     * Adds the size 'exact length' validation if the exactLengthValue does not equal -1.
     *
     * 'Exact length' means that the array/collection must have a size that matches this value.
     *
     * @param sizeAnnotation the annotation to obtain the size values
     * @param fieldName the name of the field being validated
     * @param variableName the name of the variable that is assigned back to the fieldName
     */
    private fun CodeBlock.Builder.handleExactLength(sizeAnnotation: AnnotationMirror, fieldName: String,
                                                    variableName: String, fieldType: FieldType): CodeBlock.Builder {

        val exactLengthValue: Long = getAnnotationValueObject(sizeAnnotation, "value") as Long? ?: return this

        if (exactLengthValue == -1L) {
            return this
        }

        val lengthProperty = fieldType.lengthProperty
        return this.beginControlFlow("if ($variableName.$lengthProperty != $exactLengthValue)", variableName, exactLengthValue)

                .addSizeException(fieldType, fieldName,
                        "Expected $lengthProperty: '$exactLengthValue', " +
                                """actual $lengthProperty: '" + $variableName.$lengthProperty + "'""")

                .endControlFlow()
    }

    /**
     * Adds an exception that prepends an error message that is common across all of the 'Size' validations.
     */
    private fun CodeBlock.Builder.addSizeException(fieldType: FieldType, fieldName: String,
                                                   exceptionText: String): CodeBlock.Builder {

        return this.addException("Invalid ${fieldType.label} ${fieldType.lengthProperty} for field '$fieldName'. " +
                exceptionText)
    }

    /**
     * Defines the type of field being used.
     *
     * The 'Size' annotation supports arrays and collections, and the generated code syntax must change depending
     * on which type is used.
     */
    enum class FieldType(val label: String, val lengthProperty: String) {
        ARRAY("array", "length"),
        COLLECTION("collection", "size()");
    }
}
