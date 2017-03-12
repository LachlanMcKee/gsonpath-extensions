package gsonpath.extension;

import gsonpath.AutoGsonAdapter;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import gsonpath.NonNull;
import android.support.annotation.Size;

import java.util.Collection;

@AutoGsonAdapter
public class ExtensionModelClass {
    @NonNull
    @Size(min = 0, max = 10, multiple = 2, value = 6)
    int[] sizeTestArray;

    @Size(min = 0, max = 10, multiple = 2, value = 6)
    Collection<Integer> sizeTestCollection;

    @FloatRange(from = 0.0d, to = 5.0d)
    double floatRangeTest;

    @IntRange(from = 0, to = 5)
    int intRangeTest;

    @StringDefExample
    String stringDefExampleTest;

    @IntDefExample
    int intDefExampleTest;
}
