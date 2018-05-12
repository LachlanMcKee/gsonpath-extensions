package gsonpath.extension.def.stringdef;

import com.google.auto.service.AutoService;
import gsonpath.compiler.GsonPathExtension;

/**
 * Note: AutoService only works when annotating a Java class.
 */
@SuppressWarnings("All")
@AutoService(GsonPathExtension.class)
public class StringDefGsonPathFieldValidator extends StringDefGsonPathFieldValidatorImpl {
    public StringDefGsonPathFieldValidator() {

    }
}
