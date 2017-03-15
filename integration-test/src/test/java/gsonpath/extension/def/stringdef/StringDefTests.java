package gsonpath.extension.def.stringdef;

import gsonpath.extension.TestUtil;
import org.junit.Assert;
import org.junit.Test;

public class StringDefTests {

    private StringDefModel getValue(String jsonString) {
        return TestUtil.executeFromJson(StringDefModel.class, jsonString);
    }

    @SuppressWarnings("all")
    @Test
    public void givenValidValue_whenJsonParsed_thenFieldEqualsAnnotationStringReference() {
        StringDefModel model = getValue("{\"value\": \"1\"}");

        // Note: We are deliberating checking the String reference as opposed to using .equals
        Assert.assertTrue(model.getValue() == StringDefExample.VALUE_1);
    }

    @Test
    public void givenNoValue_whenJsonParsed_thenFieldIsNull() {
        StringDefModel model = getValue("{}");
        Assert.assertNull(model.getValue());
    }

    @Test
    public void givenInvalidValue_whenJsonParsed_thenThrowsException() {
        TestUtil.expectException(StringDefModel.class, "{\"value\": \"3\"}", "Unexpected String '3' for field 'value'");
    }
}

