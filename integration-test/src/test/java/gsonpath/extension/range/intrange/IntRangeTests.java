package gsonpath.extension.range.intrange;

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
public class IntRangeTests {

    @RunWith(Parameterized.class)
    public static class Tests {
        private final Class<? extends IntRangeModel.BaseModel> modelClass;

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {IntRangeModel.IntModel.class}, {IntRangeModel.LongModel.class}
            });
        }

        public Tests(Class<? extends IntRangeModel.BaseModel> modelClass) {
            this.modelClass = modelClass;
        }

        @Test
        public void givenNoValue_whenJsonParsed_thenFieldIsNull() {
            IntRangeModel.BaseModel model = executeFromJson(modelClass, "{}");
            Assert.assertNull(model.getValue());
        }

        @Test
        public void givenMinValue_whenJsonParsed_thenFieldIsMinValue() {
            IntRangeModel.BaseModel model = executeFromJson(modelClass, "{value:0}");
            assertValue(model, 0, 0L);
        }

        @Test
        public void givenMaxValue_whenJsonParsed_thenFieldIsMinValue() {
            IntRangeModel.BaseModel model = executeFromJson(modelClass, "{value:5}");
            assertValue(model, 5, 5L);
        }

        @Test
        public void givenBelowMinValue_whenJsonParsed_thenThrowsException() {
            TestUtil.expectException(modelClass, "{value:-1}",
                    "Invalid 'from' range for value. Expected: '>= 0', Found '-1'");
        }

        @Test
        public void givenAboveMaxValue_whenJsonParsed_thenThrowsException() {
            TestUtil.expectException(modelClass, "{value:6}",
                    "Invalid 'to' range for value. Expected: '<= 5', Found '6'");
        }

        private void assertValue(IntRangeModel.BaseModel model, int intValue, long longValue) {
            if (modelClass == IntRangeModel.IntModel.class) {
                Assert.assertEquals(intValue, (int) ((IntRangeModel.IntModel) model).getValue());
            } else {
                Assert.assertEquals(longValue, (long) ((IntRangeModel.LongModel) model).getValue());
            }
        }
    }

}

