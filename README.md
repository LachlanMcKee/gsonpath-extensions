# Gson Path Extensions - Android

An extension library for Gson Path that adds validation for fields annotated with [Android Support Library annotations](https://developer.android.com/reference/android/support/annotation/package-summary.html) annotations.
* This library currently supports validation for `@FloatRange`, `@IntRange`, `@StringDef` and `@IntDef`.

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

    // Extension - Android Support Library 'FloatRange' Annotation
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
    // Extension - Android Support Library 'IntRange' Annotation
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
    // Extension - Android Support Library 'Size' Annotation
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
    // Extension - Android Support Library 'Int Def' Annotation
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
    // Extension - Android Support Library 'String Def' Annotation
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

## Download
This library is available on Maven, you can add it to your project using the following gradle dependencies:

```gradle
apt 'net.lachlanmckee:gsonpath-extension-android:1.1.0'
```
