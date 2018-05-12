package gsonpath.extension.range

import com.squareup.javapoet.CodeBlock
import gsonpath.extension.addException

/**
 * Creates the 'range' code validation code block.
 *
 * @param value the value being inspected
 * @param isFrom whether this is a 'from' or a 'to' range inspection
 * @param isInclusive whether the range validation is inclusive of the value
 * @param fieldName the name of the field being validated
 * @param variableName the name of the variable that is assigned back to the fieldName
 */
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