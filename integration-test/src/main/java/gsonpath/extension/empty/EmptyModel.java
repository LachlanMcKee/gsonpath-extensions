package gsonpath.extension.empty;

import gsonpath.AutoGsonAdapter;
import gsonpath.extension.annotation.EmptyToNull;

public interface EmptyModel {

    interface BaseStringModel {
        @EmptyToNull
        String getValue();
    }

    interface Nullable {
        @AutoGsonAdapter
        interface StringModel extends BaseStringModel {
        }
    }

    interface NonNull {
        @AutoGsonAdapter
        interface StringModel extends BaseStringModel {
            @EmptyToNull
            @gsonpath.NonNull
            String getValue();
        }
    }

}
