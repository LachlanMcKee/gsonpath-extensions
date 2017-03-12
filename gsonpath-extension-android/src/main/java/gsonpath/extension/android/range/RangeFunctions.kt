package gsonpath.extension.android.range

import com.squareup.javapoet.CodeBlock
import gsonpath.extension.android.addException

fun CodeBlock.Builder.handleRangeValue(value: String,
                                       isFrom: Boolean,
                                       isInclusive: Boolean,
                                       fieldName: String,
                                       variableName: String): CodeBlock.Builder {

    val comparisonOperator: String =
            if (isFrom) {
                if (isInclusive) "<" else "<="
            } else {
                if (isInclusive) ">" else ">="
            }

    val expectedOperator: String =
            if (isFrom) {
                if (isInclusive) ">=" else ">"
            } else {
                if (isInclusive) "<=" else "<"
            }

    val label: String = if (isFrom) "from" else "to"

    return this.beginControlFlow("if ($variableName $comparisonOperator $value)")
            .addException("Invalid '$label' range for $fieldName. Expected: '$expectedOperator $value', " +
                    """Found '" + $variableName + "'""")
            .endControlFlow()
}