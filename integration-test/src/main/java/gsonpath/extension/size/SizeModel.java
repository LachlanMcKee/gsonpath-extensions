package gsonpath.extension.size;

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
            @gsonpath.extension.annotation.Size(min = 1, max = 3)
            Integer[] getValue();
        }

        @AutoGsonAdapter
        interface CollectionModel extends BaseCollectionModel {
            @android.support.annotation.Size(min = 1, max = 3)
            List<Integer> getValue();
        }
    }

    interface Multiple {
        @AutoGsonAdapter
        interface ArrayModel extends BaseArrayModel {
            @gsonpath.extension.annotation.Size(multiple = 2)
            Integer[] getValue();
        }

        @AutoGsonAdapter
        interface CollectionModel extends BaseCollectionModel {
            @android.support.annotation.Size(multiple = 2)
            List<Integer> getValue();
        }
    }

    interface ExactSize {
        @AutoGsonAdapter
        interface ArrayModel extends BaseArrayModel {
            @gsonpath.extension.annotation.Size(value = 1)
            Integer[] getValue();
        }

        @AutoGsonAdapter
        interface CollectionModel extends BaseCollectionModel {
            @android.support.annotation.Size(value = 1)
            List<Integer> getValue();
        }
    }

}

