package gsonpath.extension.range.floatrange;

import android.support.annotation.FloatRange;
import gsonpath.AutoGsonAdapter;

interface FloatRangeModel {

    interface BaseModel<T> {
        T getValue();
    }

    interface BaseFloatModel extends BaseModel<Float> {
    }

    interface BaseDoubleModel extends BaseModel<Double> {
    }

    interface Inclusive {
        @AutoGsonAdapter
        interface FloatModel extends BaseFloatModel {
            @FloatRange(from = 0, to = 5)
            Float getValue();
        }

        @AutoGsonAdapter
        interface DoubleModel extends BaseDoubleModel {
            @FloatRange(from = 0, to = 5)
            Double getValue();
        }
    }

    interface Exclusive {
        @AutoGsonAdapter
        interface FloatModel extends BaseFloatModel {
            @FloatRange(from = 0, to = 5, fromInclusive = false, toInclusive = false)
            Float getValue();
        }

        @AutoGsonAdapter
        interface DoubleModel extends BaseDoubleModel {
            @FloatRange(from = 0, to = 5, fromInclusive = false, toInclusive = false)
            Double getValue();
        }
    }
}

