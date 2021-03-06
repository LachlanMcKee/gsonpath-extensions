# Gson Path Extensions

An extension library for Gson Path that adds validation that does not exist in the core Gson Path library. The library supports [Android Support Library](https://developer.android.com/reference/android/support/annotation/package-summary.html) annotations (without including the dependency), but also includes the `FloatRange`, `IntRange` and `Size` for non-Android projects.
* This library currently supports validation for `@FloatRange`, `@IntRange`, `@StringDef` and `@IntDef`.
* The `@EmptyToNull` annotation bundled in the library will convert empty strings, arrays, collections or maps to null.
   * If the value marked with `@EmptyToNull` is also marked with a `@NonNull` (or similar) annotation, it will throw an exception.

## FloatRange example
The following is an example of `@FloatRange` annotation validation.

### Model
```java
@AutoGsonAdapter
interface FloatModel extends BaseFloatModel {
    @FloatRange(from = 0, to = 5, fromInclusive = false, toInclusive = false)
    Float getValue();
}
```

### Generated validation
```java
// Gsonpath Extensions
if (value_value != null) {

    // Extension - 'FloatRange' Annotation
    if (value_value <= 0.0) {
        throw new com.google.gson.JsonParseException("Invalid 'from' range for value. Expected: '> 0.0', Found '" + value_value + "'");
    }
    if (value_value >= 5.0) {
        throw new com.google.gson.JsonParseException("Invalid 'to' range for value. Expected: '< 5.0', Found '" + value_value + "'");
    }
}
```

## IntRange example
The following is an example of `@IntRange` annotation validation.

### Model
```java
@AutoGsonAdapter
interface IntModel extends BaseModel<Integer> {
    @IntRange(from = 0, to = 5)
    Integer getValue();
}
```

### Generated validation
```java
// Gsonpath Extensions
if (value_value != null) {
    // Extension - 'IntRange' Annotation
    if (value_value < 0) {
        throw new com.google.gson.JsonParseException("Invalid 'from' range for value. Expected: '>= 0', Found '" + value_value + "'");
    }
    if (value_value > 5) {
        throw new com.google.gson.JsonParseException("Invalid 'to' range for value. Expected: '<= 5', Found '" + value_value + "'");
    }
}
```

## Size example
The following is an example of `@Size` annotation validation.

### Model
```java
@AutoGsonAdapter
interface ArrayModel extends BaseArrayModel {
    @Size(min = 0, max = 6, multiple = 2, value = 2)
    Integer[] getValue();
}
```

### Generated validation
```java
// Gsonpath Extensions
if (value_value != null) {
    // Extension - 'Size' Annotation
    if (value_value.length != 2) {
        throw new com.google.gson.JsonParseException("Invalid array length for field 'value'. Expected length: '2', actual length: '" + value_value.length + "'");
    }
    if (value_value.length < 0) {
        throw new com.google.gson.JsonParseException("Invalid array length for field 'value'. Expected minimum: '0', actual minimum: '" + value_value.length + "'");
    }
    if (value_value.length > 6) {
        throw new com.google.gson.JsonParseException("Invalid array length for field 'value'. Expected maximum: '6', actual maximum: '" + value_value.length + "'");
    }
    if (value_value.length % 2 != 0) {
        throw new com.google.gson.JsonParseException("Invalid array length for field 'value'. length of '" + value_value.length + "' is not a multiple of 2");
    }
}
```

## IntDef example
The following is an example of `@IntDef` annotation validation.

### Model
```java
@AutoGsonAdapter
interface IntDefModel {
    @IntDefExample
    Integer getValue();
    
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({VALUE_1, VALUE_2})
    @interface IntDefExample {
        int VALUE_1 = 1;
        int VALUE_2 = 2;
    }
}
```

### Generated validation
```java
// Gsonpath Extensions
if (value_value != null) {
    // Extension - 'Int Def' Annotation
    switch (value_value) {
        case 1:
        case 2:
            break;
        
        default:
            throw new com.google.gson.JsonParseException("Unexpected Int '" + value_value + "' for field 'value'");
    }
}
```


## StringDef example
The following is an example of `@StringDef` annotation validation. Note that the switch statement reassigns the string value to the actual constant from the `@StringDef` annotation. This is to appease the support library linting rules in Android Studio.

### Model
```java
@AutoGsonAdapter
interface StringDefModel {
    @StringDefExample
    String getValue();
    
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({VALUE_1, VALUE_2})
    @interface StringDefExample {
        String VALUE_1 = "1";
        String VALUE_2 = "2";
    }
}
```

### Generated validation
```java
// Gsonpath Extensions
if (value_value != null) {
    // Extension - 'String Def' Annotation
    switch (value_value) {
        case StringDefExample.VALUE_1:
            value_value = StringDefExample.VALUE_1;
            break;
        
        case StringDefExample.VALUE_2:
            value_value = StringDefExample.VALUE_2;
            break;
        
        default:
            throw new com.google.gson.JsonParseException("Unexpected String '" + value_value + "' for field 'value'");
    }
}
```


## EmptyToNull example
The following is an example of `@EmptyToNull` annotation validation.

### Model
```java
@AutoGsonAdapter
interface StringDefModel {
    @EmptyToNull
    String getNullableString();
    
    @EmptyToNull
    String[] getNullableArray();
    
    @EmptyToNull
    Collection<String> getNullableCollection();
    
    @EmptyToNull
    Map<String, String> getNullableMap();
    
    @NonNull
    @EmptyToNull
    String getNonNullString();
    
    @NonNull
    @EmptyToNull
    String[] getNonNullArray();
    
    @NonNull
    @EmptyToNull
    Collection<String> getNonNullCollection();
    
    @NonNull
    @EmptyToNull
    Map<String, String> getNonNullMap();
}
```

### Generated validation (paraphrased)
```java
// Extension - 'EmptyToNull' Annotation
if (nullableString.trim().length() == 0) {
    nullableString = null;
}
if (nullableArray.length == 0) {
    nullableArray = null;
}
if (nullableCollection.size() == 0) {
    nullableCollection = null;
}
if (nullableMap.size() == 0) {
    nullableMap = null;
}
if (nonNullString.trim().length() == 0) {
    throw new com.google.gson.JsonParseException("JSON element 'nonNullString' cannot be blank");
}
if (nonNullArray.length == 0) {
    throw new com.google.gson.JsonParseException("JSON element 'nonNullArray' cannot be blank");
}
if (nonNullCollection.size() == 0) {
    throw new com.google.gson.JsonParseException("JSON element 'nonNullCollection' cannot be blank");
}
if (nonNullMap.size() == 0) {
    throw new com.google.gson.JsonParseException("JSON element 'nonNullMap' cannot be blank");
}
```

## Download
This library is available on Maven, you can add it to your project using the following gradle dependencies:

```gradle
apt 'net.lachlanmckee:gsonpath-extensions:1.1.0'
```
