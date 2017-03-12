package gsonpath.extension.android.def.stringdef

import com.squareup.javapoet.CodeBlock
import com.sun.source.tree.AnnotationTree
import com.sun.source.tree.AssignmentTree
import com.sun.source.tree.IdentifierTree
import com.sun.source.tree.MemberSelectTree
import com.sun.source.util.TreePathScanner
import com.sun.source.util.Trees
import com.sun.tools.javac.tree.JCTree
import gsonpath.compiler.GsonPathExtension
import gsonpath.compiler.addNewLine
import gsonpath.compiler.addWithNewLine
import gsonpath.extension.android.addException
import gsonpath.extension.android.def.DefAnnotationMirrors
import gsonpath.extension.android.def.getDefAnnotationMirrors
import gsonpath.extension.android.getAnnotationValue
import gsonpath.model.FieldInfo
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationValue

open class AndroidStringDefGsonPathFieldValidatorImpl : GsonPathExtension {

    override fun getExtensionName(): String {
        return "Android Support Library 'String Def' Annotation"
    }

    override fun createFieldReadCodeBlock(processingEnv: ProcessingEnvironment, fieldInfo: FieldInfo,
                                       variableName: String): CodeBlock? {

        val element = fieldInfo.element ?: return null
        val defAnnotationMirrors: DefAnnotationMirrors = getDefAnnotationMirrors(element,
                "android.support.annotation", "StringDef") ?: return null

        val defAnnotationValues: AnnotationValue = getAnnotationValue(defAnnotationMirrors.defAnnotationMirror,
                "value") ?: return null

        val annotationElement = defAnnotationMirrors.annotationMirror.annotationType.asElement()
        val treesInstance = Trees.instance(processingEnv)

        val stringDefConstants: List<String>? = AnnotationVisitor().scan(
                treesInstance.getPath(annotationElement, defAnnotationMirrors.defAnnotationMirror, defAnnotationValues),
                null)

        if (stringDefConstants == null || stringDefConstants.isEmpty()) {
            return null
        }

        val validationBuilder = CodeBlock.builder()
        validationBuilder.beginControlFlow("switch ($variableName)")

        stringDefConstants.forEach { it ->

            val constant =
                    if (!it.contains(".")) {
                        // Append the enclosing class name
                        "$annotationElement.$it"
                    } else {
                        it
                    }

            validationBuilder.addWithNewLine("""case $constant:""")
                    .indent()
                    .addStatement("$variableName = $constant")
                    .addStatement("break")
                    .unindent()
                    .addNewLine()
        }

        validationBuilder.addWithNewLine("default:")
                .indent()
                .addException("""Unexpected String '" + $variableName + "' for field '${fieldInfo.fieldName}'""")
                .unindent()

        validationBuilder.endControlFlow()

        val validationCodeBlock = validationBuilder.build()
        if (!validationCodeBlock.isEmpty) {
            return validationCodeBlock
        }
        return null
    }

    private class AnnotationVisitor : TreePathScanner<List<String>?, Void>() {

        override fun visitAnnotation(node: AnnotationTree, p: Void?): List<String>? {
            for (expressionTree in node.arguments) {
                if (expressionTree !is AssignmentTree) {
                    continue
                }

                val variable = expressionTree.variable
                if (variable !is IdentifierTree || !variable.name.contentEquals("value")) {
                    continue
                }

                val expression = expressionTree.expression as JCTree.JCNewArray
                return expression.elems.map(JCTree.JCExpression::toString)
            }

            return null
        }

        override fun visitAssignment(at: AssignmentTree, p: Void?): List<String>? {
            return null
        }

        override fun visitMemberSelect(mst: MemberSelectTree, p: Void?): List<String>? {
            return null
        }

        override fun visitIdentifier(it: IdentifierTree, p: Void?): List<String>? {
            return null
        }
    }

}
