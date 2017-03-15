package gsonpath.extension.size;

import gsonpath.extension.TestUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static gsonpath.extension.TestUtil.executeFromJson;

@RunWith(Enclosed.class)
public class SizeTests {

    @RunWith(Parameterized.class)
    public static class MinAndMaxTests {
        private final Class<? extends SizeModel.BaseModel> modelClass;

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {SizeModel.MinAndMax.ArrayModel.class},
                    {SizeModel.MinAndMax.CollectionModel.class}
            });
        }

        public MinAndMaxTests(Class<? extends SizeModel.BaseModel> modelClass) {
            this.modelClass = modelClass;
        }

        @Test
        public void givenNoValue_whenJsonParsed_thenFieldIsNull() {
            SizeModel.BaseModel model = executeFromJson(modelClass, "{}");
            Assert.assertNull(model.getValue());
        }

        @Test
        public void givenArrayLengthWithinRange_whenJsonParsed_thenArrayIsDeserialized() {
            SizeModel.BaseModel model = executeFromJson(modelClass, "{value: [1, 2]}");
            assertValue(model, new Integer[]{1, 2});
        }

        @Test
        public void givenArraySizeTooSmall_whenJsonParsed_thenThrowsException() {
            String expectedMessage;
            if (modelClass == SizeModel.MinAndMax.ArrayModel.class) {
                expectedMessage = "Invalid array length for field 'value'. Expected minimum: '1', actual minimum: '0'";
            } else {
                expectedMessage = "Invalid collection size() for field 'value'. Expected minimum: '1', actual minimum: '0'";
            }

            TestUtil.expectException(modelClass, "{value: []}", expectedMessage);
        }

        @Test
        public void givenArraySizeTooLarge_whenJsonParsed_thenThrowsException() {
            String expectedMessage;
            if (modelClass == SizeModel.MinAndMax.ArrayModel.class) {
                expectedMessage = "Invalid array length for field 'value'. Expected maximum: '3', actual maximum: '4'";
            } else {
                expectedMessage = "Invalid collection size() for field 'value'. Expected maximum: '3', actual maximum: '4'";
            }

            TestUtil.expectException(modelClass, "{value: [1, 2, 3, 4]}", expectedMessage);
        }
    }

    @RunWith(Parameterized.class)
    public static class MultipleTests {
        private final Class<? extends SizeModel.BaseModel> modelClass;

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {SizeModel.Multiple.ArrayModel.class},
                    {SizeModel.Multiple.CollectionModel.class}
            });
        }

        public MultipleTests(Class<? extends SizeModel.BaseModel> modelClass) {
            this.modelClass = modelClass;
        }

        @Test
        public void givenNoValue_whenJsonParsed_thenFieldIsNull() {
            SizeModel.BaseModel model = executeFromJson(modelClass, "{}");
            Assert.assertNull(model.getValue());
        }

        @Test
        public void givenArraySizeEqualsMultiple_whenJsonParsed_thenArrayIsDeserialized() {
            SizeModel.BaseModel model = executeFromJson(modelClass, "{value: [1, 2]}");
            assertValue(model, new Integer[]{1, 2});
        }

        @Test
        public void givenArraySizeIsAMultiple_whenJsonParsed_thenArrayIsDeserialized() {
            SizeModel.BaseModel model = executeFromJson(modelClass, "{value: [1, 2, 3, 4]}");
            assertValue(model, new Integer[]{1, 2, 3, 4});
        }

        @Test
        public void givenArraySizeIsNotAMultiple_whenJsonParsed_thenThrowsException() {
            String expectedMessage;
            if (modelClass == SizeModel.Multiple.ArrayModel.class) {
                expectedMessage = "Invalid array length for field 'value'. length of '3' is not a multiple of 2";
            } else {
                expectedMessage = "Invalid collection size() for field 'value'. size() of '3' is not a multiple of 2";
            }

            TestUtil.expectException(modelClass, "{value: [1, 2, 3]}", expectedMessage);
        }
    }

    @RunWith(Parameterized.class)
    public static class ExactSizeTests {
        private final Class<? extends SizeModel.BaseModel> modelClass;

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {SizeModel.ExactSize.ArrayModel.class},
                    {SizeModel.ExactSize.CollectionModel.class}
            });
        }

        public ExactSizeTests(Class<? extends SizeModel.BaseModel> modelClass) {
            this.modelClass = modelClass;
        }

        @Test
        public void givenNoValue_whenJsonParsed_thenFieldIsNull() {
            SizeModel.BaseModel model = executeFromJson(modelClass, "{}");
            Assert.assertNull(model.getValue());
        }

        @Test
        public void givenArraySizeIsExact_whenJsonParsed_thenArrayIsDeserialized() {
            SizeModel.BaseModel model = executeFromJson(modelClass, "{value: [1]}");
            assertValue(model, new Integer[]{1});
        }

        @Test
        public void givenArraySizeDoesNotMatch_whenJsonParsed_thenThrowsException() {
            String expectedMessage;
            if (modelClass == SizeModel.ExactSize.ArrayModel.class) {
                expectedMessage = "Invalid array length for field 'value'. Expected length: '1', actual length: '2'";
            } else {
                expectedMessage = "Invalid collection size() for field 'value'. Expected size(): '1', actual size(): '2'";
            }

            TestUtil.expectException(modelClass, "{value: [1, 2]}", expectedMessage);
        }
    }

    private static void assertValue(SizeModel.BaseModel model, Integer[] array) {
        if (model instanceof SizeModel.BaseArrayModel) {
            Assert.assertTrue(Arrays.equals(array, ((SizeModel.BaseArrayModel) model).getValue()));
        } else {
            Assert.assertTrue(Arrays.asList(array).equals(((SizeModel.BaseCollectionModel) model).getValue()));
        }
    }

}

