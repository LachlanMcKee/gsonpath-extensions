package gsonpath.extension.range.intrange;

import android.support.annotation.IntRange;
import gsonpath.AutoGsonAdapter;

interface IntRangeModel {

    interface BaseModel<T> {
        T getValue();
    }

    @AutoGsonAdapter
    interface IntModel extends BaseModel<Integer> {
        @IntRange(from = 0, to = 5)
        Integer getValue();
    }

    @AutoGsonAdapter
    interface LongModel extends BaseModel<Long> {
        @IntRange(from = 0, to = 5)
        Long getValue();
    }
}

