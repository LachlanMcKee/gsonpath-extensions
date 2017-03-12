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

open class AndroidIntDefGsonPathFieldValidatorImpl : GsonPathExtension {

    override fun getExtensionName(): String {
        return "Android Support Library 'Int Def' Annotation"
    }

    override fun createFieldReadCodeBlock(processingEnv: ProcessingEnvironment, fieldInfo: FieldInfo,
                                       variableName: String): CodeBlock? {

        val element = fieldInfo.element ?: return null
        val defAnnotationMirrors: DefAnnotationMirrors = getDefAnnotationMirrors(element,
                "android.support.annotation", "IntDef") ?: return null

        val validationBuilder = CodeBlock.builder()
        validationBuilder.beginControlFlow("switch ($variableName)")

        val intDefValues: List<*> = getAnnotationValueObject(defAnnotationMirrors.defAnnotationMirror, "value")
                as List<*>? ?: return null

        intDefValues.forEach { it ->
            validationBuilder.addWithNewLine("""case ${(it as AnnotationValue).value}:""")
        }

        validationBuilder.indent()
                .addStatement("break")
                .unindent()
                .addNewLine()
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
