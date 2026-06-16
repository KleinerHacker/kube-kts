# 값 처리

Kube KTS에서 값은 Helm과 유사하게 관리됩니다. 이를 통해 구성을 외부화하고 오버레이를 사용하여 다양한 환경에 맞게 배포를 사용자 정의할 수 있습니다.

## 값 오버레이

값 오버레이 메커니즘은 Helm과 정확히 동일하게 작동합니다.
- **KTS 스크립트**: 병합은 Kube KTS가 Helm 호환 로직을 통해 내부적으로 처리합니다.
- **기존 YAML**: 표준 Helm 명령이 처리 전에 병합을 수행합니다.

## KTS에서 값 다루기

Kotlin 스크립트에서는 값을 검색하고 검증하기 위한 여러 내장 함수를 사용할 수 있습니다. 이 함수들은 값 구조를 탐색하기 위해 **점으로 구분된 경로**를 사용합니다.

!!! warning "보안: 임포트 제한"
    기본적으로 KTS 스크립트는 `import` 문이나 완전 정규화된 클래스 이름
    (예: `java.lang.Runtime`)을 **허용하지 않습니다**. 사전 구성된 기본 임포트로 제공되는 타입만 사용할 수 있습니다.

    `--unsafe` 플래그를 사용하면 이러한 제한을 해제할 수 있습니다.

!!! note "중요"
    기본적으로 모든 경로는 YAML 파일의 `values:` 루트 요소를 기준으로 합니다. 경로 시작 부분에 `values.`를 포함할 필요가 없습니다.

### 지원되는 타입

다음 네이티브 타입은 값의 직접 검색을 지원합니다.
- `String`, `Boolean`
- `Int`, `Long`, `Short`, `Byte`
- `Double`, `Float`

YAML의 값이 요청한 타입과 일치하지 않으면 예외가 발생합니다.

---

## 핵심 함수

### `exists`
지정된 경로에 값이나 구조가 존재하는지 확인합니다.

**확인 후 사용:**
```kotlin
if (exists("spec.replicas")) {
    // ...
}
```

**조건부 실행(람다):**
```kotlin
exists("spec.replicas") {
    // This block only runs if the path exists
}
```

### `value` 와 `valueOrNull`
특정 타입의 단일 값을 검색합니다.

**반드시 존재해야 함(없으면 예외 발생):**
```kotlin
val replicas = value<Int>("spec.replicas")
```

**선택적 값(없으면 `null` 반환):**
```kotlin
val replicas = valueOrNull<Int>("spec.replicas")
```

### `array` 와 `arrayOrNull`
값 또는 객체의 목록을 처리합니다.

**직접 검색:**
```kotlin
val tags = array<String>("metadata.tags")
```

**반복(람다):**
```kotlin
array("metadata.tags") { tag ->
    println("Found tag: $tag")
}

// With index
array("metadata.tags") { index, tag ->
    println("Tag #$index: $tag")
}
```

### `map` 와 `mapOrNull`
키-값 쌍(딕셔너리)을 처리합니다.

**직접 검색:**
```kotlin
val labels = map<String>("metadata.labels")
```

**반복(람다):**
```kotlin
map<String>("metadata.labels") { key, value ->
    println("$key = $value")
}
```

---

## 복잡한 구조(중첩 접근)

복잡한 객체나 배열 내부의 중첩된 값에 접근해야 하는 경우, 타입을 지정하지 않는 람다 변형을 사용할 수 있습니다. 이는 새로운 `ValueAccess` 스코프(`it`로 사용 가능)를 제공합니다.

### 중첩 객체
```kotlin
value("spec.resources") {
    // 'it' now refers to 'spec.resources'
    val cpu = it.value<String>("limits.cpu")
    val memory = it.value<String>("limits.memory")
}
```

### 객체 배열
```kotlin
array("spec.containers") {
    // 'it' is a ValueAccess for the current container object in the list
    val name = it.value<String>("name")
    val image = it.value<String>("image")
}
```

### 객체 맵
```kotlin
map("spec.services") { name, config ->
    // 'config' is a ValueAccess for the object associated with the key 'name'
    val port = config.value<Int>("port")
}
```
