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

/**
 * A {@link GsonPathExtension} that supports the '@StringDef' Android Support Library annotation.
 *
 * This extension has a quirk in which the string value must be overridden with the actual constant from the
 * '@StringDef` annotation.
 *
 * This is due to how the lint validation works in Android Studio. The linting requires that the exact same String
 * reference is used, rather than another String instance that has the same equality.
 */
open class AndroidStringDefGsonPathFieldValidatorImpl : GsonPathExtension {

    override fun getExtensionName(): String {
        return "Android Support Library 'String Def' Annotation"
    }

    override fun createFieldReadCodeBlock(processingEnv: ProcessingEnvironment, fieldInfo: FieldInfo,
                                          variableName: String): CodeBlock? {

        val defAnnotationMirrors: DefAnnotationMirrors = getDefAnnotationMirrors(fieldInfo.element,
                "android.support.annotation", "StringDef") ?: return null

        // The annotation values reference which contains the String constants.
        val defAnnotationValues: AnnotationValue = getAnnotationValue(defAnnotationMirrors.defAnnotationMirror,
                "value") ?: return null

        //
        // The standard javax AnnotationMirror does not expose the ability to retrieve the actual constant variable
        // names, as it normally returns the value of the String. Therefore we use the 'TreePathScanner' to inspect
        // the actual source code and obtain the variable names.
        //
        val annotationElement = defAnnotationMirrors.annotationMirror.annotationType.asElement()
        val treesInstance = Trees.instance(processingEnv)
        val stringDefConstants: List<String>? = AnnotationValueConstantsVisitor().scan(
                treesInstance.getPath(annotationElement, defAnnotationMirrors.defAnnotationMirror, defAnnotationValues),
                null)

        // If there are no String constants in the 'StringDef' abort.
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

                    //
                    // The StringDef annotation requires the String constant to be referenced, therefore we must
                    // reassign the String value to the constant.
                    //
                    .addStatement("$variableName = $constant")
                    .addStatement("break")
                    .unindent()
                    .addNewLine()
        }

        // Throw an exception if an unexpected String is found.
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

    /**
     * Obtains the String constant variable name from the annotation rather than the String value itself.
     *
     * This is required due to the StringDef annotation requiring the String reference to be used and not a different
     * String with the same equality.
     */
    private class AnnotationValueConstantsVisitor : TreePathScanner<List<String>?, Void>() {

        override fun visitAnnotation(node: AnnotationTree, p: Void?): List<String>? {
            for (expressionTree in node.arguments) {
                if (expressionTree !is AssignmentTree) {
                    continue
                }

                // Find the 'value' property of the annotation.
                val variable = expressionTree.variable
                if (variable !is IdentifierTree || !variable.name.contentEquals("value")) {
                    continue
                }

                // Obtain the String constant variable names.
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
