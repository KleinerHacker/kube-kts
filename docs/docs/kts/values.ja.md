# 値の取り扱い

Kube KTS では、値は Helm と同様に管理されます。これにより、構成を外部化し、オーバーレイを使ってさまざまな環境向けにデプロイをカスタマイズできます。

## 値のオーバーレイ

値のオーバーレイの仕組みは Helm とまったく同じように機能します。
- **KTS スクリプト**: マージは Kube KTS によって Helm 互換のロジックで内部的に処理されます。
- **従来の YAML**: 標準の Helm コマンドが処理前にマージを実行します。

## KTS での値の取り扱い

Kotlin スクリプトでは、値を取得・検証するためのいくつかの組み込み関数を利用できます。これらの関数は、値の構造をたどるために**ドット区切りのパス**を使用します。

!!! warning "セキュリティ: インポート制限"
    デフォルトでは、KTS スクリプトは `import` 文や完全修飾クラス名
    （例: `java.lang.Runtime`）を**許可しません**。事前構成されたデフォルトインポートで提供される型のみ使用できます。

    `--unsafe` フラグを使うとこれらの制限を解除できます。

!!! note "重要"
    デフォルトでは、すべてのパスは YAML ファイルの `values:` ルート要素を基準とします。パスの先頭に `values.` を含める必要はありません。

### サポートされる型

次のネイティブ型は値の直接取得をサポートしています。
- `String`、`Boolean`
- `Int`、`Long`、`Short`、`Byte`
- `Double`、`Float`

YAML 内の値が要求された型と一致しない場合、例外がスローされます。

---

## コア関数

### `exists`
指定したパスに値または構造が存在するかどうかを確認します。

**確認して使用する:**
```kotlin
if (exists("spec.replicas")) {
    // ...
}
```

**条件付き実行（ラムダ）:**
```kotlin
exists("spec.replicas") {
    // This block only runs if the path exists
}
```

### `value` と `valueOrNull`
特定の型の単一の値を取得します。

**必ず存在する必要がある（存在しない場合は例外をスロー）:**
```kotlin
val replicas = value<Int>("spec.replicas")
```

**任意の値（存在しない場合は `null` を返す）:**
```kotlin
val replicas = valueOrNull<Int>("spec.replicas")
```

### `array` と `arrayOrNull`
値またはオブジェクトのリストを扱います。

**直接取得:**
```kotlin
val tags = array<String>("metadata.tags")
```

**反復処理（ラムダ）:**
```kotlin
array("metadata.tags") { tag ->
    println("Found tag: $tag")
}

// With index
array("metadata.tags") { index, tag ->
    println("Tag #$index: $tag")
}
```

### `map` と `mapOrNull`
キーと値のペア（辞書）を扱います。

**直接取得:**
```kotlin
val labels = map<String>("metadata.labels")
```

**反復処理（ラムダ）:**
```kotlin
map<String>("metadata.labels") { key, value ->
    println("$key = $value")
}
```

---

## 複雑な構造（ネストアクセス）

複雑なオブジェクトや配列の中のネストされた値にアクセスする必要がある場合は、型を指定しないラムダのバリアントを使用できます。これにより、新しい `ValueAccess` スコープ（`it` として利用可能）が提供されます。

### ネストされたオブジェクト
```kotlin
value("spec.resources") {
    // 'it' now refers to 'spec.resources'
    val cpu = it.value<String>("limits.cpu")
    val memory = it.value<String>("limits.memory")
}
```

### オブジェクトの配列
```kotlin
array("spec.containers") {
    // 'it' is a ValueAccess for the current container object in the list
    val name = it.value<String>("name")
    val image = it.value<String>("image")
}
```

### オブジェクトのマップ
```kotlin
map("spec.services") { name, config ->
    // 'config' is a ValueAccess for the object associated with the key 'name'
    val port = config.value<Int>("port")
}
```
