package gsonpath.extension.def.intdef;

import gsonpath.extension.TestUtil;
import org.junit.Assert;
import org.junit.Test;

public class IntDefTests {

    private IntDefModel getValue(String jsonString) {
        return TestUtil.executeFromJson(IntDefModel.class, jsonString);
    }

    @Test
    public void givenValidValue_whenJsonParsed_thenParsesWithoutErrors() {
        IntDefModel model = getValue("{\"value\": 2}");
        Assert.assertEquals(2, (long) model.getValue());
    }

    @Test
    public void givenNoValue_whenJsonParsed_thenFieldIsNull() {
        IntDefModel model = getValue("{}");
        Assert.assertNull(model.getValue());
    }

    @Test
    public void givenInvalidValue_whenJsonParsed_thenThrowsException() {
        TestUtil.expectException(IntDefModel.class, "{\"value\": 3}", "Unexpected Int '3' for field 'value'");
    }
}
