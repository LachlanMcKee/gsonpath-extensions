package gsonpath.extension.android.size

import com.squareup.javapoet.CodeBlock
import gsonpath.ProcessingException
import gsonpath.compiler.GsonPathExtension
import gsonpath.compiler.isFieldCollectionType
import gsonpath.extension.android.addException
import gsonpath.extension.android.getAnnotationMirror
import gsonpath.extension.android.getAnnotationValueObject
import gsonpath.model.FieldInfo
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.type.ArrayType

open class AndroidSizeGsonPathFieldValidatorImpl : GsonPathExtension {

    override fun getExtensionName(): String {
        return "Android Support Library 'Size' Annotation"
    }

    override fun createFieldReadCodeBlock(processingEnv: ProcessingEnvironment, fieldInfo: FieldInfo,
                                       variableName: String): CodeBlock? {

        val element = fieldInfo.element ?: return null

        val sizeAnnotation: AnnotationMirror = getAnnotationMirror(element,
                "android.support.annotation", "Size") ?: return null

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

    private fun CodeBlock.Builder.addSizeException(fieldType: FieldType, fieldName: String,
                                                   exceptionText: String): CodeBlock.Builder {

        return this.addException("Invalid ${fieldType.label} ${fieldType.lengthProperty} for field '$fieldName'. " +
                exceptionText)
    }

    enum class FieldType(val label: String, val lengthProperty: String) {
        ARRAY("array", "length"), COLLECTION("collection", "size()");
    }
}
