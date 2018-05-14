package gsonpath.extension.empty

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import gsonpath.ProcessingException
import gsonpath.compiler.ExtensionFieldMetadata
import gsonpath.compiler.GsonPathExtension
import gsonpath.compiler.isFieldCollectionType
import gsonpath.extension.addException
import gsonpath.extension.annotation.EmptyToNull
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.type.ArrayType

/**
 * A {@link GsonPathExtension} that supports the '@Size' annotation.
 */
class EmptyToNullGsonPathFieldValidator : GsonPathExtension {

    override val extensionName: String
        get() = "'EmptyToNull' Annotation"

    override fun createFieldReadCodeBlock(processingEnvironment: ProcessingEnvironment,
                                          extensionFieldMetadata: ExtensionFieldMetadata): CodeBlock? {

        val (fieldInfo, variableName, jsonPath, isRequired) = extensionFieldMetadata

        if (fieldInfo.getAnnotation(EmptyToNull::class.java) == null) {
            return null
        }

        val fieldCollectionType: Boolean =
            try {
                isFieldCollectionType(processingEnvironment, fieldInfo.typeMirror)
            } catch (e: Exception) {
                false
            }

        val fieldType: FieldType =
            when {
                (fieldInfo.typeMirror is ArrayType) -> FieldType.ARRAY
                fieldCollectionType -> FieldType.COLLECTION
                (fieldInfo.typeName == ClassName.get(String::class.java)) -> FieldType.STRING

                else ->
                    throw ProcessingException("Unexpected type found for field annotated with 'EmptyToNull', only " +
                        "string, array, map, or collection classes may be used.", fieldInfo.element)
            }

        val validationBuilder = CodeBlock.builder()
            .beginControlFlow("if ($variableName${fieldType.emptyCheck})")
            .apply {
                if (isRequired) {
                    addException("JSON element '$jsonPath' cannot be blank")
                } else {
                    addStatement("$variableName = null")
                }
            }
            .endControlFlow()

        return validationBuilder.build()
    }

    /**
     * Defines the type of field being used.
     *
     * The 'Size' annotation supports arrays and collections, and the generated code syntax must change depending
     * on which type is used.
     */
    enum class FieldType(val emptyCheck: String) {
        ARRAY(".length == 0"),
        COLLECTION(".size() == 0"),
        STRING(".trim().length() == 0"),
        MAP(".size() == 0")
    }
}
