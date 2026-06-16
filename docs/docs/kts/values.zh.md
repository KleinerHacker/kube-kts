# 值处理

在 Kube KTS 中，值的管理方式与 Helm 类似。这使你能够将配置外部化，并使用 overlay 为不同环境定制你的部署。

## 值 Overlay

值的 overlay 机制与 Helm 中的工作方式完全相同。
- **KTS 脚本**：合并由 Kube KTS 内部通过 Helm 兼容的逻辑处理。
- **传统 YAML**：标准 Helm 命令在处理前执行合并。

## 在 KTS 中处理值

在 Kotlin 脚本中，你可以使用若干内置函数来检索和验证值。这些函数使用**以点分隔的路径**来导航你的值结构。

!!! warning "安全性：导入限制"
    默认情况下，KTS 脚本**不允许**使用 `import` 语句或完全限定的类名
    （例如 `java.lang.Runtime`）。只能使用通过预配置默认导入提供的类型。

    使用 `--unsafe` 标志可解除这些限制。

!!! note "重要"
    默认情况下，所有路径都相对于 YAML 文件的 `values:` 根元素。你无需在路径开头包含 `values.`。

### 支持的类型

以下原生类型支持直接检索值：
- `String`、`Boolean`
- `Int`、`Long`、`Short`、`Byte`
- `Double`、`Float`

如果 YAML 中的某个值与请求的类型不匹配，将抛出异常。

---

## 核心函数

### `exists`
检查给定路径上是否存在某个值或结构。

**检查并使用：**
```kotlin
if (exists("spec.replicas")) {
    // ...
}
```

**条件执行（Lambda）：**
```kotlin
exists("spec.replicas") {
    // This block only runs if the path exists
}
```

### `value` 与 `valueOrNull`
检索特定类型的单个值。

**必须存在（缺失时抛出异常）：**
```kotlin
val replicas = value<Int>("spec.replicas")
```

**可选值（缺失时返回 `null`）：**
```kotlin
val replicas = valueOrNull<Int>("spec.replicas")
```

### `array` 与 `arrayOrNull`
处理值或对象的列表。

**直接检索：**
```kotlin
val tags = array<String>("metadata.tags")
```

**迭代（Lambda）：**
```kotlin
array("metadata.tags") { tag ->
    println("Found tag: $tag")
}

// With index
array("metadata.tags") { index, tag ->
    println("Tag #$index: $tag")
}
```

### `map` 与 `mapOrNull`
处理键值对（字典）。

**直接检索：**
```kotlin
val labels = map<String>("metadata.labels")
```

**迭代（Lambda）：**
```kotlin
map<String>("metadata.labels") { key, value ->
    println("$key = $value")
}
```

---

## 复杂结构（嵌套访问）

如果你需要访问复杂对象或数组中的嵌套值，可以使用不指定类型的 lambda 变体。这会提供一个新的 `ValueAccess` 作用域（以 `it` 形式提供）。

### 嵌套对象
```kotlin
value("spec.resources") {
    // 'it' now refers to 'spec.resources'
    val cpu = it.value<String>("limits.cpu")
    val memory = it.value<String>("limits.memory")
}
```

### 对象数组
```kotlin
array("spec.containers") {
    // 'it' is a ValueAccess for the current container object in the list
    val name = it.value<String>("name")
    val image = it.value<String>("image")
}
```

### 对象映射
```kotlin
map("spec.services") { name, config ->
    // 'config' is a ValueAccess for the object associated with the key 'name'
    val port = config.value<Int>("port")
}
```
