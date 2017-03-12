package gsonpath.extension.android.def

import gsonpath.extension.android.getAnnotationMirror
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element

data class DefAnnotationMirrors(val annotationMirror: AnnotationMirror, val defAnnotationMirror: AnnotationMirror)

fun getDefAnnotationMirrors(element: Element, packageName: String, className: String): DefAnnotationMirrors? {

    val definitionAnnotation: AnnotationMirror = element.annotationMirrors?.find {
        getAnnotationMirror(it.annotationType.asElement(), packageName, className) != null
    } ?: return null

    val stringDefAnnotationMirror = getAnnotationMirror(definitionAnnotation.annotationType.asElement(),
            packageName, className) ?: return null

    return DefAnnotationMirrors(definitionAnnotation, stringDefAnnotationMirror)
}