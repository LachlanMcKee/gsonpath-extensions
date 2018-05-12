package gsonpath.extension.size

import gsonpath.extension.TestUtil
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import java.util.Arrays

import gsonpath.extension.TestUtil.executeFromJson

@RunWith(Enclosed::class)
object SizeTests {
    @RunWith(Parameterized::class)
    class MinAndMaxTests(private val modelClass: Class<out SizeModel.BaseModel<*>>) {

        @Test
        fun givenNoValue_whenJsonParsed_thenFieldIsNull() {
            val model = executeFromJson(modelClass, "{}")
            Assert.assertNull(model.value)
        }

        @Test
        fun givenArrayLengthWithinRange_whenJsonParsed_thenArrayIsDeserialized() {
            val model = executeFromJson(modelClass, "{value: [1, 2]}")
            assertValue(model, arrayOf(1, 2))
        }

        @Test
        fun givenArraySizeTooSmall_whenJsonParsed_thenThrowsException() {
            val expectedMessage = if (modelClass == SizeModel.MinAndMax.ArrayModel::class.java) {
                "Invalid array length for field 'value'. Expected minimum: '1', actual minimum: '0'"
            } else {
                "Invalid collection size() for field 'value'. Expected minimum: '1', actual minimum: '0'"
            }

            TestUtil.expectException(modelClass, "{value: []}", expectedMessage)
        }

        @Test
        fun givenArraySizeTooLarge_whenJsonParsed_thenThrowsException() {
            val expectedMessage = if (modelClass == SizeModel.MinAndMax.ArrayModel::class.java) {
                "Invalid array length for field 'value'. Expected maximum: '3', actual maximum: '4'"
            } else {
                "Invalid collection size() for field 'value'. Expected maximum: '3', actual maximum: '4'"
            }

            TestUtil.expectException(modelClass, "{value: [1, 2, 3, 4]}", expectedMessage)
        }

        companion object {
            @JvmStatic
            @Parameterized.Parameters
            fun data(): Collection<Array<Any>> {
                return Arrays.asList(arrayOf<Any>(SizeModel.MinAndMax.ArrayModel::class.java), arrayOf<Any>(SizeModel.MinAndMax.CollectionModel::class.java))
            }
        }
    }

    @RunWith(Parameterized::class)
    class MultipleTests(private val modelClass: Class<out SizeModel.BaseModel<*>>) {

        @Test
        fun givenNoValue_whenJsonParsed_thenFieldIsNull() {
            val model = executeFromJson(modelClass, "{}")
            Assert.assertNull(model.value)
        }

        @Test
        fun givenArraySizeEqualsMultiple_whenJsonParsed_thenArrayIsDeserialized() {
            val model = executeFromJson(modelClass, "{value: [1, 2]}")
            assertValue(model, arrayOf(1, 2))
        }

        @Test
        fun givenArraySizeIsAMultiple_whenJsonParsed_thenArrayIsDeserialized() {
            val model = executeFromJson(modelClass, "{value: [1, 2, 3, 4]}")
            assertValue(model, arrayOf(1, 2, 3, 4))
        }

        @Test
        fun givenArraySizeIsNotAMultiple_whenJsonParsed_thenThrowsException() {
            val expectedMessage = if (modelClass == SizeModel.Multiple.ArrayModel::class.java) {
                "Invalid array length for field 'value'. length of '3' is not a multiple of 2"
            } else {
                "Invalid collection size() for field 'value'. size() of '3' is not a multiple of 2"
            }

            TestUtil.expectException(modelClass, "{value: [1, 2, 3]}", expectedMessage)
        }

        companion object {
            @JvmStatic
            @Parameterized.Parameters
            fun data(): Collection<Array<Any>> {
                return Arrays.asList(arrayOf<Any>(SizeModel.Multiple.ArrayModel::class.java), arrayOf<Any>(SizeModel.Multiple.CollectionModel::class.java))
            }
        }
    }

    @RunWith(Parameterized::class)
    class ExactSizeTests(private val modelClass: Class<out SizeModel.BaseModel<*>>) {

        @Test
        fun givenNoValue_whenJsonParsed_thenFieldIsNull() {
            val model = executeFromJson(modelClass, "{}")
            Assert.assertNull(model.value)
        }

        @Test
        fun givenArraySizeIsExact_whenJsonParsed_thenArrayIsDeserialized() {
            val model = executeFromJson(modelClass, "{value: [1]}")
            assertValue(model, arrayOf(1))
        }

        @Test
        fun givenArraySizeDoesNotMatch_whenJsonParsed_thenThrowsException() {
            val expectedMessage = if (modelClass == SizeModel.ExactSize.ArrayModel::class.java) {
                "Invalid array length for field 'value'. Expected length: '1', actual length: '2'"
            } else {
                "Invalid collection size() for field 'value'. Expected size(): '1', actual size(): '2'"
            }

            TestUtil.expectException(modelClass, "{value: [1, 2]}", expectedMessage)
        }

        companion object {
            @JvmStatic
            @Parameterized.Parameters
            fun data(): Collection<Array<Any>> {
                return Arrays.asList(arrayOf<Any>(SizeModel.ExactSize.ArrayModel::class.java), arrayOf<Any>(SizeModel.ExactSize.CollectionModel::class.java))
            }
        }
    }

    private fun assertValue(model: SizeModel.BaseModel<*>, array: Array<Int>) {
        if (model is SizeModel.BaseArrayModel) {
            Assert.assertTrue(Arrays.equals(array, model.value))
        } else {
            Assert.assertTrue(Arrays.asList(*array) == (model as SizeModel.BaseCollectionModel).value)
        }
    }
}

