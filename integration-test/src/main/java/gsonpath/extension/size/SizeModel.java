package gsonpath.extension.size;

import android.support.annotation.Size;
import gsonpath.AutoGsonAdapter;

import java.util.List;

interface SizeModel {

    interface BaseModel<T> {
        T getValue();
    }

    interface BaseArrayModel extends BaseModel<Integer[]> {
    }

    interface BaseCollectionModel extends BaseModel<List<Integer>> {
    }

    interface MinAndMax {
        @AutoGsonAdapter
        interface ArrayModel extends BaseArrayModel {
            @Size(min = 1, max = 3)
            Integer[] getValue();
        }

        @AutoGsonAdapter
        interface CollectionModel extends BaseCollectionModel {
            @Size(min = 1, max = 3)
            List<Integer> getValue();
        }
    }

    interface Multiple {
        @AutoGsonAdapter
        interface ArrayModel extends BaseArrayModel {
            @Size(multiple = 2)
            Integer[] getValue();
        }

        @AutoGsonAdapter
        interface CollectionModel extends BaseCollectionModel {
            @Size(multiple = 2)
            List<Integer> getValue();
        }
    }

    interface ExactSize {
        @AutoGsonAdapter
        interface ArrayModel extends BaseArrayModel {
            @Size(value = 1)
            Integer[] getValue();
        }

        @AutoGsonAdapter
        interface CollectionModel extends BaseCollectionModel {
            @Size(value = 1)
            List<Integer> getValue();
        }
    }

}

