package gsonpath.extension;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import gsonpath.GsonPath;
import gsonpath.TestGsonTypeFactory;
import org.junit.Assert;

public class TestUtil {

    public static <T> T executeFromJson(Class<T> clazz, String jsonString) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(GsonPath.createTypeAdapterFactory(TestGsonTypeFactory.class));

        Gson gson = builder.create();

        return gson.fromJson(jsonString, clazz);
    }

    public static void expectException(Class clazz, String jsonString, String message) {
        JsonParseException exception = null;
        try {
            executeFromJson(clazz, jsonString);

        } catch (JsonParseException e) {
            exception = e;
        }

        if (exception != null) {
            Assert.assertEquals(message, exception.getMessage());
            return;
        }

        Assert.fail("Expected exception was not thrown.");
    }
}
