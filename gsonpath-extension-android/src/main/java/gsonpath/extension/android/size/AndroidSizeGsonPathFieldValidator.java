package gsonpath.extension.android.size;

import com.google.auto.service.AutoService;
import gsonpath.compiler.GsonPathExtension;

/**
 * Note: AutoService only works when annotating a Java class.
 */
@SuppressWarnings("All")
@AutoService(GsonPathExtension.class)
public class AndroidSizeGsonPathFieldValidator extends AndroidSizeGsonPathFieldValidatorImpl {
    public AndroidSizeGsonPathFieldValidator() {

    }
}
