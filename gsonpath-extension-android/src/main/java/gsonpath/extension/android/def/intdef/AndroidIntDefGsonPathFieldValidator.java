package gsonpath.extension.android.def.intdef;

import com.google.auto.service.AutoService;
import gsonpath.compiler.GsonPathExtension;

/**
 * Note: AutoService only works when annotating a Java class.
 */
@SuppressWarnings("All")
@AutoService(GsonPathExtension.class)
public class AndroidIntDefGsonPathFieldValidator extends AndroidIntDefGsonPathFieldValidatorImpl {
    public AndroidIntDefGsonPathFieldValidator() {

    }
}
