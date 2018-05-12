package gsonpath.extension.def.range.intrange;

import com.google.auto.service.AutoService;
import gsonpath.compiler.GsonPathExtension;

/**
 * Note: AutoService only works when annotating a Java class.
 */
@SuppressWarnings("All")
@AutoService(GsonPathExtension.class)
public class IntRangeGsonPathFieldValidator extends IntRangeGsonPathFieldValidatorImpl {
}
