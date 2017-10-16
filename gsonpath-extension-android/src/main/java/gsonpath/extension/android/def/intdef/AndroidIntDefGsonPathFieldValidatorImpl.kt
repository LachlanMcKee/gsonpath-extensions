package gsonpath.extension.android.def.intdef

import com.squareup.javapoet.CodeBlock
import gsonpath.compiler.GsonPathExtension
import gsonpath.compiler.addNewLine
import gsonpath.compiler.addWithNewLine
import gsonpath.extension.android.addException
import gsonpath.extension.android.def.DefAnnotationMirrors
import gsonpath.extension.android.def.getDefAnnotationMirrors
import gsonpath.extension.android.getAnnotationValueObject
import gsonpath.model.FieldInfo
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationValue

/**
 * A {@link GsonPathExtension} that supports the '@IntDef' Android Support Library annotation.
 */
open class AndroidIntDefGsonPathFieldValidatorImpl : GsonPathExtension {

    override fun getExtensionName(): String {
        return "Android Support Library 'Int Def' Annotation"
    }

    override fun createFieldReadCodeBlock(processingEnv: ProcessingEnvironment, fieldInfo: FieldInfo,
                                          variableName: String): CodeBlock? {

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
                .addException("""Unexpected Int '" + $variableName + "' for field '${fieldInfo.fieldName}'""")
                .unindent()

        validationBuilder.endControlFlow()

        val validationCodeBlock = validationBuilder.build()
        if (!validationCodeBlock.isEmpty) {
            return validationCodeBlock
        }
        return null
    }

}
