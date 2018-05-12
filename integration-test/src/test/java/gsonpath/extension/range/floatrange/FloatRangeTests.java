package gsonpath.extension.range.floatrange;

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
public class FloatRangeTests {

    @RunWith(Parameterized.class)
    public static class InclusiveTests {
        private final Class<? extends FloatRangeModel.BaseModel> modelClass;

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                {FloatRangeModel.Inclusive.FloatModel.class}, {FloatRangeModel.Inclusive.DoubleModel.class}
            });
        }

        public InclusiveTests(Class<? extends FloatRangeModel.BaseModel> modelClass) {
            this.modelClass = modelClass;
        }

        @Test
        public void givenNoValue_whenJsonParsed_thenFieldIsNull() {
            FloatRangeModel.BaseModel model = executeFromJson(modelClass, "{}");
            Assert.assertNull(model.getValue());
        }

        @Test
        public void givenMinValue_whenJsonParsed_thenFieldIsMinValue() {
            FloatRangeModel.BaseModel model = executeFromJson(modelClass, "{value:0.0}");
            assertValue(model, 0.0d, 0.0f);
        }

        @Test
        public void givenMaxValue_whenJsonParsed_thenFieldIsMinValue() {
            FloatRangeModel.BaseModel model = executeFromJson(modelClass, "{value:5.0}");
            assertValue(model, 5.0d, 5.0f);
        }

        @Test
        public void givenBelowMinValue_whenJsonParsed_thenThrowsException() {
            TestUtil.expectException(modelClass, "{value:-0.1}",
                "Invalid 'from' range for value. Expected: '>= 0.0', Found '-0.1'");
        }

        @Test
        public void givenAboveMaxValue_whenJsonParsed_thenThrowsException() {
            TestUtil.expectException(modelClass, "{value:5.1}",
                "Invalid 'to' range for value. Expected: '<= 5.0', Found '5.1'");
        }
    }

    @RunWith(Parameterized.class)
    public static class ExclusiveTests {
        private final Class<? extends FloatRangeModel.BaseModel> modelClass;

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                {FloatRangeModel.Exclusive.FloatModel.class}, {FloatRangeModel.Exclusive.DoubleModel.class}
            });
        }

        public ExclusiveTests(Class<? extends FloatRangeModel.BaseModel> modelClass) {
            this.modelClass = modelClass;
        }

        @Test
        public void givenNoValue_whenJsonParsed_thenFieldIsNull() {
            FloatRangeModel.BaseModel model = executeFromJson(modelClass, "{}");
            Assert.assertNull(model.getValue());
        }

        @Test
        public void givenJustAboveMinValueAndBelowMax_whenJsonParsed_thenFieldIsMinValue() {
            FloatRangeModel.BaseModel model = executeFromJson(modelClass, "{value:0.1}");
            assertValue(model, 0.1d, 0.1f);
        }

        @Test
        public void givenJustBelowMaxValueAndAboveMinValue_whenJsonParsed_thenFieldIsMinValue() {
            FloatRangeModel.BaseModel model = executeFromJson(modelClass, "{value:4.9}");
            assertValue(model, 4.9d, 4.9f);
        }

        @Test
        public void givenMinValue_whenJsonParsed_thenThrowsException() {
            TestUtil.expectException(modelClass, "{value:0.0}",
                "Invalid 'from' range for value. Expected: '> 0.0', Found '0.0'");
        }

        @Test
        public void givenMaxValue_whenJsonParsed_thenThrowsException() {
            TestUtil.expectException(modelClass, "{value:5.0}",
                "Invalid 'to' range for value. Expected: '< 5.0', Found '5.0'");
        }

        @Test
        public void givenBelowMinValue_whenJsonParsed_thenThrowsException() {
            TestUtil.expectException(modelClass, "{value:-0.1}",
                "Invalid 'from' range for value. Expected: '> 0.0', Found '-0.1'");
        }

        @Test
        public void givenAboveMaxValue_whenJsonParsed_thenThrowsException() {
            TestUtil.expectException(modelClass, "{value:5.1}",
                "Invalid 'to' range for value. Expected: '< 5.0', Found '5.1'");
        }
    }

    private static void assertValue(FloatRangeModel.BaseModel model, double doubleValue, float floatValue) {
        if (model instanceof FloatRangeModel.BaseFloatModel) {
            Assert.assertEquals(floatValue, ((FloatRangeModel.BaseFloatModel) model).getValue(), 0);
        } else {
            Assert.assertEquals(doubleValue, ((FloatRangeModel.BaseDoubleModel) model).getValue(), 0);
        }
    }
}

