package gsonpath.extension.def.intdef

import com.squareup.javapoet.CodeBlock
import gsonpath.compiler.ExtensionFieldMetadata
import gsonpath.compiler.GsonPathExtension
import gsonpath.compiler.addNewLine
import gsonpath.compiler.addWithNewLine
import gsonpath.extension.addException
import gsonpath.extension.def.DefAnnotationMirrors
import gsonpath.extension.def.getDefAnnotationMirrors
import gsonpath.extension.getAnnotationValueObject
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationValue

/**
 * A {@link GsonPathExtension} that supports the '@IntDef' annotation.
 */
class IntDefGsonPathFieldValidator : GsonPathExtension {
    override val extensionName: String
        get() = "'Int Def' Annotation"

    override fun createFieldReadCodeBlock(processingEnvironment: ProcessingEnvironment,
                                          extensionFieldMetadata: ExtensionFieldMetadata): CodeBlock? {

        val (fieldInfo, variableName) = extensionFieldMetadata

        val defAnnotationMirrors: DefAnnotationMirrors = getDefAnnotationMirrors(fieldInfo.element,
            "android.support.annotation", "IntDef") ?: return null

        val validationBuilder = CodeBlock.builder()
        validationBuilder.beginControlFlow("switch ($variableName)")

        // The integer constants within the 'IntDef#values' property.
        val intDefValues: List<*> = getAnnotationValueObject(defAnnotationMirrors.defAnnotationMirror, "value")
            as List<*>? ?: return null

        // Create a 'case' for each valid integer.
        intDefValues.forEach { it ->
            validationBuilder.addWithNewLine("""case ${(it as AnnotationValue).value}:""")
        }

        validationBuilder.indent()
            .addStatement("break")
            .unindent()
            .addNewLine()

            // Create a 'default' that throws an exception if an unexpected integer is found.
            .addWithNewLine("default:")
            .indent()
            .addException("""Unexpected Int '" + $variableName + "' for JSON element '${extensionFieldMetadata.jsonPath}'""")
            .unindent()

        validationBuilder.endControlFlow()

        val validationCodeBlock = validationBuilder.build()
        if (!validationCodeBlock.isEmpty) {
            return validationCodeBlock
        }
        return null
    }

}
