package gsonpath.extension.empty

import gsonpath.extension.TestUtil
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

@RunWith(Enclosed::class)
object EmptyTests {
    class StringTests {
        @Test
        fun givenBlankPassedToNonNullModel_whenJsonParsed_thenExpectException() {
            TestUtil.expectException(EmptyModel.NonNull.StringModel::class.java, "{value:\"\"}",
                "JSON element 'value' cannot be blank")
        }

        @Test
        fun givenBlankPassedToNullableModel_whenJsonParsed_thenExpectNull() {
            val model = TestUtil.executeFromJson(EmptyModel.Nullable.StringModel::class.java, "{value:\"\"}")
            Assert.assertEquals(null, model.value)
        }

        @Test
        fun givenValuePassedToNonNullModel_whenJsonParsed_thenExpectValue() {
            val model = TestUtil.executeFromJson(EmptyModel.Nullable.StringModel::class.java, "{value:\"a\"}")
            Assert.assertEquals("a", model.value)
        }

        @Test
        fun givenValuePassedToNullableModel_whenJsonParsed_thenExpectValue() {
            val model = TestUtil.executeFromJson(EmptyModel.Nullable.StringModel::class.java, "{value:\"a\"}")
            Assert.assertEquals("a", model.value)
        }
    }
}

