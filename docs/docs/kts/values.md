# Values Handling

## Values Overlay

The values overlay (merging) works like Helm. For KTS scripts the merging happens
in Kube KTS itself via Helm. For legacy YAML files the running Helm command is
merging the values.

## How to use in Kube KTS (Kotlin Scripts)

There are a lot of basic functions to work with values. At the end these following 
function groups exist:

- `exists`: Check that a value exists (path exists)
- `value`: Get a value from given path
- `array`: Get an array from given path
- `map`: Get a key value map of given path. Key is always a String.

Basically, all of these methods require a JSON Path to handle values.

!!! note "Important"
    The values YAML must start with `values:` always. In KTS you do not need to
    include this in the given path. This is used as a prefix for the JSON Path.

---

The JSON Path is a string that describes the location of a value in a JSON document. It uses a 
dot notation to specify the path to the value. For example, `$.spec.replicas` 
refers to the `replicas` field in the `spec` object.

### General Notices

If the type for the value you want is not equal to the type in YAML, you will get an exception.

---

Basically, only these native types are supported:
- String
- Byte
- Short
- Int
- Long
- Float
- Double
- Boolean

These types you can use with all methods, `array`, too.

If the value you want is a complex structure, all methods provide a way
to handle it via lambda. See the examples below.

### Method `exists`

This method checks if a value exists at the given path.

There are two variants:

**Direct Usage**

```kotlin
val valExists = exists("$.spec.replicas")
if (valExists) {
    // do something
}
```

or like this in direct way:

```kotlin
if (exists("$.spec.replicas")) {
    // do something
}
```

In this case the value is returned by the method.

**Indirect Usage**

```kotlin
exists("$.spec.replicas") {
    // do something
}
```

Here the "body" of the method is executed only if the value exists.

### Method `value`

The `value` method returns the value at the given path.

There are two basic variants:

**Null Allowed**

```kotlin
val replicas = valueOrNull<Int>("$.spec.replicas")
```

Here you get an Integer or, if not exists, null. Please remember that 
Kotlin Script is null safety. See [Kotlin Null Safety](https://kotlinlang.org/docs/null-safety.html)
for more information.

This is the usage for native types. If you want to get a complex value
like a structure, you need to use the `value` method in this way:

```kotlin
valueOrNull("$.spec.replicas") {
    // do something
}
```

In this case you get a new value named `it` that contains the sub path
of the values YAML. You can call all known methods from here to access 
the values of the substructure.

**Must Exist (Null Forbidden)**

```kotlin
val replicas = value<Int>("$.spec.replicas")
```

In this case the value must exist. If it is not, an exception is thrown
and the script is aborted. This follows in Kube KTS command fails.

Alternatively, for more complex values, you can use the `value` method in this way:

```kotlin
value("$.spec.replicas") {
    // do something
}
```

You get a new value named `it` that contains the sub path of the values YAML.
You can call all known methods from here to access the values of the substructure.

### Method `array`

The `array` method returns the array at the given path.

There are two basic variants:

**Null Allowed**

```kotlin
val valArray = arrayOrNull<String>("$.spec.replicas")
```

Here you get an array or, if not exists, null. Please remember that 
Kotlin Script is null safety. See [Kotlin Null Safety](https://kotlinlang.org/docs/null-safety.html)
for more information.

Alternatively, you can use the `array` method in this way:

```kotlin
arrayOrNull("$.spec.replicas") {
    // do something
}
```

In this case you get a new value named `it` that contains one element
of the array. It means internally it loops over the array and commits 
each element. If the array is empty or null, the body is not executed.

If it is required, you can get the index value, too:

```kotlin
arrayOrNull("$.spec.replicas") { index, value ->
    // do something
}
```

Here you get both, the index and the value of the array.

**Must Exist (Null Forbidden)**

The methods works like `array` methods above. But if the path does 
not exists, an exception is thrown. The command will fail.

```kotlin
val valArray = array<String>("$.spec.replicas")
array("$.spec.replicas") {
    // do something
}
array("$.spec.replicas") { index, value ->
    // do something
}
```

### Method `map`

The `map` method returns the map at the given path.

**Null Allowed**

```kotlin
val valMap = mapOrNull<Int>("$.spec.replicas")
```

You get the map or, if not exists, null. Please remember that 
Kotlin Script is null safety. See [Kotlin Null Safety](https://kotlinlang.org/docs/null-safety.html)
for more information.

Alternatively, you can use the direct way:

```kotlin
mapOrNull<Int>("$.spec.replicas") { key, value -> 
    // do something
}
```

In this case the map is looped internally over all keys and commits 
each key and its value to the lambda. If the map is empty or null, 
the body is not executed.

For more complex values, you can use this way:

```kotlin
mapOrNull("$.spec.replicas") { key, value ->
    // do something
}
```

In this case you get a new value of the substructure in parameter 
`value`. Here you can call all known methods from here to access 
the values of the substructure.

**Must Exist (Null Forbidden)**

The methods works like `map` methods above.

```kotlin
val valMap = map<Int>("$.spec.replicas")
map<Int>("$.spec.replicas") { key, value ->
    // do something
}
map("$.spec.replicas") { key, value ->
    // do something
}
```