package gsonpath.extension;

import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.Size;
import gsonpath.*;

import java.util.Collection;

@AutoGsonAdapter
public interface ExtensionModelInterface {
    @NonNull
    @Size(min = 0, max = 10, multiple = 2, value = 6)
    int[] getSizeTest();

    @Size(min = 0, max = 10, multiple = 2, value = 6)
    Collection<Integer> getSizeTestCollection();

    @FloatRange(from = 0.0d, to = 5.0d)
    double getFloatRangeTest();

    @IntRange(from = 0, to = 5)
    int getIntRangeTest();
}
