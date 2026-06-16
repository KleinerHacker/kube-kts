# ConfigMap DSL

`configMap` DSL은 Kubernetes ConfigMap 리소스를 구성하는 데 사용됩니다. ConfigMap은
기밀이 아닌 구성 데이터를 키-값 쌍으로 저장하며, Pod는 이를 환경 변수, 명령줄 인수 또는
볼륨을 통해 마운트된 파일로 사용할 수 있습니다.

!!! warning "보안: 임포트 제한"
    기본적으로 KTS 스크립트는 `import` 문이나 완전 정규화된 클래스 이름
    (예: `java.lang.Runtime`)을 **허용하지 않습니다**. 사전 구성된 기본 임포트로
    제공되는 타입만 사용할 수 있습니다.

    `--unsafe` 플래그를 사용하면 이러한 제한을 해제할 수 있습니다.

!!! note "ConfigMap은 기밀이 아닌 데이터를 위한 것입니다"
    ConfigMap은 암호화되지 않으므로 비밀번호나 토큰과 같은 민감한 정보를 저장해서는 안 됩니다.
    기밀 데이터에는 Secret을 사용하세요.

## 기본 사용법

최소한의 ConfigMap 구성에는 `metadata`와 `spec` 내의 항목이 최소 하나 필요합니다.

```kotlin
configMap {
    metadata("my-config") {
        namespace = "default"
    }

    spec {
        addData("application.name", "demo")
        addData("log.level", "INFO")
    }
}
```

## 상세 예제

다음 예제는 문자열 데이터, 바이너리 데이터 및 불변성을 보여줍니다.

```kotlin
configMap {
    metadata("full-config") {
        namespace = "production"
        labels {
            label("app", "demo")
        }
        annotations {
            annotation("description", "Application configuration")
        }
    }

    spec {
        // String data — rendered under `data:`
        data {
            entry("log.level", "DEBUG")
            entry(
                "application.yaml",
                """
                server:
                  port: 8080
                """.trimIndent()
            )
        }

        // Binary data — rendered base64-encoded under `binaryData:`
        binaryData {
            entry("logo.png", logoBytes)
        }

        // Once created, the ConfigMap can no longer be changed
        immutable = true
    }
}
```

## 구성 참조

### 메타데이터(`metadata`)

| 속성 | 타입 | 설명 |
| :--- | :--- | :--- |
| `name` | `String` | ConfigMap의 이름(첫 번째 인수로 전달됨). |
| `namespace` | `String?` | 리소스의 네임스페이스. |
| `generateName` | `String?` | 고유한 이름 생성을 위한 선택적 접두사. |
| `labels { label(key, value) }` | `Map<String, String>` | ConfigMap 리소스의 레이블. |
| `annotations { annotation(key, value) }` | `Map<String, String>` | ConfigMap 리소스의 어노테이션. |

### ConfigMap 명세(`spec`)

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `addData(key, value)` | `data`에 단일 문자열 항목을 추가합니다. |
| `data { entry(key, value) }` | 블록을 통해 `data`에 여러 문자열 항목을 추가합니다. |
| `addBinaryData(key, value)` | `binaryData`에 단일 바이너리(`ByteArray`) 항목을 추가합니다. |
| `binaryData { entry(key, value) }` | 블록을 통해 `binaryData`에 여러 바이너리 항목을 추가합니다. |
| `immutable` | `true`인 경우 ConfigMap은 생성 후 업데이트할 수 없습니다. |

!!! note "`data` 와 `binaryData`"
    `data`는 UTF-8 문자열 값에 사용합니다. 렌더링된 YAML의 `data:` 아래에 직접 표시됩니다.
    `binaryData`는 임의의 바이트 콘텐츠에 사용합니다. 값은 base64로 인코딩되어 `binaryData:` 아래에 표시됩니다.
    키는 두 맵 전체에서 고유해야 합니다.
