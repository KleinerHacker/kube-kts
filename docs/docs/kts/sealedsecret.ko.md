# SealedSecret DSL

`sealedSecret` DSL은 [Bitnami Sealed Secrets](https://github.com/bitnami-labs/sealed-secrets)
(`bitnami.com/v1alpha1`)를 구성하는 데 사용됩니다. SealedSecret은 **암호화된** 데이터를
보유하며, 클러스터 내 컨트롤러만이 이를 일반 Kubernetes [Secret](secret.md)으로 복호화할 수
있습니다. 값이 암호화되어 있으므로 SealedSecret은 버전 관리에 안전하게 저장할 수 있습니다.

!!! warning "보안: 임포트 제한"
    기본적으로 KTS 스크립트는 `import` 문이나 정규화된 클래스 이름
    (예: `java.lang.Runtime`)의 사용을 **허용하지 않습니다**. 사전 구성된 기본 임포트로
    제공되는 타입만 사용할 수 있습니다.

    `--unsafe` 플래그를 사용하면 이러한 제한을 해제할 수 있습니다.

!!! note "SealedSecrets 컨트롤러가 설치되어 있어야 합니다"
    SealedSecret은 대상 클러스터에서 실행 중인 SealedSecrets 컨트롤러에 의해서만 복호화될 수
    있습니다. 암호화된 값은 해당 컨트롤러의 공개 키에 대해 `kubeseal`로 생성됩니다.

## 기본 사용법

최소한의 SealedSecret 구성에는 `metadata`와 `encryptedData` 내의 항목이 하나 이상 필요합니다.

```kotlin
sealedSecret {
    metadata("my-sealed-secret") {
        namespace = "default"
    }

    spec {
        addEncryptedData("password", "AgBy3i4OJSWK+PiTySYZZA9rO...")
    }
}
```

## 상세 예제

다음 예제는 여러 암호화 항목과 컨트롤러가 생성해야 하는 Secret을 설명하는 `template`을
보여줍니다.

```kotlin
sealedSecret {
    metadata("full-sealed-secret") {
        namespace = "production"
        labels {
            label("app", "demo")
        }
    }

    spec {
        encryptedData {
            entry("username", "AgAKv2H8x9Qm0pLrT3uVwX1yZ...")
            entry("password", "AgBy3i4OJSWK+PiTySYZZA9rO...")
        }

        // 컨트롤러가 생성하는 Secret을 설명합니다
        template {
            type = SecretSpec.Type.Opaque
            immutable = true
            metadata {
                labels {
                    label("app", "demo")
                }
                annotations {
                    annotation("description", "Application credentials")
                }
            }
        }
    }
}
```

## 구성 참조

### 메타데이터 (`metadata`)

| 속성 | 타입 | 설명 |
| :--- | :--- | :--- |
| `name` | `String` | SealedSecret의 이름이며, 첫 번째 인수로 전달됩니다. |
| `namespace` | `String?` | 리소스의 네임스페이스. |
| `generateName` | `String?` | 고유한 이름을 생성하기 위한 선택적 접두사. |
| `labels { label(key, value) }` | `Map<String, String>` | SealedSecret 리소스의 레이블. |
| `annotations { annotation(key, value) }` | `Map<String, String>` | SealedSecret 리소스의 어노테이션. |

### SealedSecret 사양 (`spec`)

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `addEncryptedData(key, value)` | `encryptedData`에 단일 암호화 항목을 추가합니다. 항목이 하나 이상 필요합니다. |
| `encryptedData { entry(key, value) }` | 블록을 통해 `encryptedData`에 여러 암호화 항목을 추가합니다. |
| `template { ... }` | 생성되는 Secret을 설명하는 선택적 템플릿([템플릿](#template) 참조). |

### 템플릿 (`template`)

`template` 블록은 컨트롤러가 복호화 후 생성하는 Secret을 설명합니다.

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `type` | 생성되는 Secret의 타입([Secret DSL](secret.md#secret)과 동일한 값). |
| `immutable` | `true`이면 생성된 Secret을 생성 후 업데이트할 수 없습니다. |
| `metadata { labels { ... }; annotations { ... } }` | 생성되는 Secret에 적용되는 레이블과 어노테이션. |
