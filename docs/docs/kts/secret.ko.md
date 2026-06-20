# Secret DSL

`secret` DSL은 Kubernetes Secret 리소스를 구성하는 데 사용됩니다. Secret은 비밀번호, 토큰,
키와 같은 소량의 민감한 데이터를 저장하며, Pod는 이를 환경 변수로 또는 볼륨을 통해 마운트된
파일로 사용할 수 있습니다.

!!! warning "보안: 임포트 제한"
    기본적으로 KTS 스크립트는 `import` 문이나 정규화된 클래스 이름
    (예: `java.lang.Runtime`)의 사용을 **허용하지 않습니다**. 사전 구성된 기본 임포트로
    제공되는 타입만 사용할 수 있습니다.

    `--unsafe` 플래그를 사용하면 이러한 제한을 해제할 수 있습니다.

!!! note "Secret은 암호화가 아니라 base64 인코딩만 됩니다"
    Secret의 값은 base64로 인코딩되어 저장되며 **암호화되지 않습니다**. Secret(또는 etcd)에
    대한 읽기 권한이 있는 사람은 누구나 이를 디코딩할 수 있습니다. 렌더링된 Secret을 버전
    관리에 커밋하지 마십시오. 버전 관리에 안전한 대안으로는 [SealedSecret](sealedsecret.md)을
    사용하십시오.

## 기본 사용법

최소한의 Secret 구성에는 `metadata`와 `spec` 내의 데이터 항목이 하나 이상 필요합니다.

```kotlin
secret {
    metadata("my-secret") {
        namespace = "default"
    }

    spec {
        addStringData("username", "admin")
        addStringData("password", "s3cr3t")
    }
}
```

## 상세 예제

다음 예제는 Secret 타입, 바이너리 데이터, 문자열 데이터 및 불변성을 보여줍니다.

```kotlin
secret {
    metadata("full-secret") {
        namespace = "production"
        labels {
            label("app", "demo")
        }
        annotations {
            annotation("description", "Application credentials")
        }
    }

    spec {
        // 내장 Secret 타입
        type = SecretSpec.Type.Opaque

        // 바이너리 데이터 —— `data:` 아래에 base64로 인코딩되어 렌더링됩니다
        data {
            entry("token", tokenBytes)
        }

        // 일반 문자열 데이터 —— `stringData:` 아래에 렌더링됩니다
        stringData {
            entry("username", "admin")
            entry("password", "s3cr3t")
        }

        // 생성된 후에는 Secret을 변경할 수 없습니다
        immutable = true
    }
}
```

## 구성 참조

### 메타데이터 (`metadata`)

| 속성 | 타입 | 설명 |
| :--- | :--- | :--- |
| `name` | `String` | Secret의 이름이며, 첫 번째 인수로 전달됩니다. |
| `namespace` | `String?` | 리소스의 네임스페이스. |
| `generateName` | `String?` | 고유한 이름을 생성하기 위한 선택적 접두사. |
| `labels { label(key, value) }` | `Map<String, String>` | Secret 리소스의 레이블. |
| `annotations { annotation(key, value) }` | `Map<String, String>` | Secret 리소스의 어노테이션. |

### Secret 사양 (`spec`)

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `type` | Secret 타입([Secret 타입](#secret) 참조). 생략 시 기본값은 `Opaque`입니다. |
| `addData(key, value)` | `data`에 단일 바이너리 (`ByteArray`) 항목을 추가합니다. |
| `data { entry(key, value) }` | 블록을 통해 `data`에 여러 바이너리 항목을 추가합니다. |
| `addStringData(key, value)` | `stringData`에 단일 일반 문자열 항목을 추가합니다. |
| `stringData { entry(key, value) }` | 블록을 통해 `stringData`에 여러 일반 문자열 항목을 추가합니다. |
| `immutable` | `true`이면 Secret을 생성 후 업데이트할 수 없습니다. |

!!! note "`data` 와 `stringData`"
    `data`는 원시 바이트 콘텐츠에 사용합니다. 값은 렌더링된 YAML의 `data:` 아래에 base64로
    인코딩되어 나타납니다. `stringData`는 UTF-8 문자열 값에 사용합니다. 이 값은 `stringData:`
    아래에 일반 형식으로 나타나며 Kubernetes에 의해 `data`로 병합됩니다. 키는 두 맵 전체에서
    고유해야 합니다.

### Secret 타입

| DSL 값 | 렌더링되는 타입 |
| :--- | :--- |
| `SecretSpec.Type.Opaque` | `Opaque` |
| `SecretSpec.Type.ServiceAccountToken` | `kubernetes.io/service-account-token` |
| `SecretSpec.Type.DockerCfg` | `kubernetes.io/dockercfg` |
| `SecretSpec.Type.DockerConfigJson` | `kubernetes.io/dockerconfigjson` |
| `SecretSpec.Type.BasicAuth` | `kubernetes.io/basic-auth` |
| `SecretSpec.Type.SshAuth` | `kubernetes.io/ssh-auth` |
| `SecretSpec.Type.Tls` | `kubernetes.io/tls` |
| `SecretSpec.Type.BootstrapToken` | `bootstrap.kubernetes.io/token` |
