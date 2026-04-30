# Values Handling

In Kube KTS, values are managed similarly to Helm. This allows you to externalize configuration and use overlays to customize your deployments for different environments.

## Values Overlay

The values overlay mechanism works just like in Helm.
- **KTS Scripts**: Merging is handled internally by Kube KTS via Helm-compatible logic.
- **Legacy YAML**: Standard Helm commands perform the merging before processing.

## Working with Values in KTS

In Kotlin scripts, you have access to several built-in functions to retrieve and validate values. These functions use a **dot-separated path** to navigate your values structure.

!!! note "Important"
    By default, all paths are relative to the `values:` root element of your YAML file. You do not need to include `values.` at the beginning of your paths.

### Supported Types

The following native types are supported for direct value retrieval:
- `String`, `Boolean`
- `Int`, `Long`, `Short`, `Byte`
- `Double`, `Float`

If a value in the YAML does not match the requested type, an exception will be thrown.

---

## Core Functions

### `exists`
Checks if a value or structure exists at the given path.

**Check and use:**
```kotlin
if (exists("spec.replicas")) {
    // ...
}
```

**Conditional execution (Lambda):**
```kotlin
exists("spec.replicas") {
    // This block only runs if the path exists
}
```

### `value` & `valueOrNull`
Retrieves a single value of a specific type.

**Must exist (throws exception if missing):**
```kotlin
val replicas = value<Int>("spec.replicas")
```

**Optional value (returns `null` if missing):**
```kotlin
val replicas = valueOrNull<Int>("spec.replicas")
```

### `array` & `arrayOrNull`
Handles lists of values or objects.

**Direct retrieval:**
```kotlin
val tags = array<String>("metadata.tags")
```

**Iteration (Lambda):**
```kotlin
array("metadata.tags") { tag ->
    println("Found tag: $tag")
}

// With index
array("metadata.tags") { index, tag ->
    println("Tag #$index: $tag")
}
```

### `map` & `mapOrNull`
Handles key-value pairs (dictionaries).

**Direct retrieval:**
```kotlin
val labels = map<String>("metadata.labels")
```

**Iteration (Lambda):**
```kotlin
map<String>("metadata.labels") { key, value ->
    println("$key = $value")
}
```

---

## Complex Structures (Nested Access)

If you need to access nested values within a complex object or array, you can use the lambda variants without specifying a type. This provides a new `ValueAccess` scope (available as `it`).

### Nested Objects
```kotlin
value("spec.resources") {
    // 'it' now refers to 'spec.resources'
    val cpu = it.value<String>("limits.cpu")
    val memory = it.value<String>("limits.memory")
}
```

### Arrays of Objects
```kotlin
array("spec.containers") {
    // 'it' is a ValueAccess for the current container object in the list
    val name = it.value<String>("name")
    val image = it.value<String>("image")
}
```

### Maps of Objects
```kotlin
map("spec.services") { name, config ->
    // 'config' is a ValueAccess for the object associated with the key 'name'
    val port = config.value<Int>("port")
}
```