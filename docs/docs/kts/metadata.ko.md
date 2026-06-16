# Metadata DSL

`metadata` DSL은 Kubernetes 리소스의 메타데이터를 정의하는 데 사용됩니다. 메타데이터는 거의 모든 Kubernetes 리소스(예: Service, Ingress, Deployment)에 존재하며, 이를 식별하고 정리하는 역할을 합니다.

## 기본 사용법

Kube KTS에서 `metadata` 블록은 리소스 내부(예: `service`, `ingress`)에 정의됩니다. 리소스의 **이름**은 `metadata` 함수의 첫 번째 인수로 전달됩니다.

### 최소 구성

최소 구성에는 리소스의 이름만 필요합니다.

```kotlin
service {
    metadata("my-service") {
        // No further properties required
    }
    // ... remaining service configuration
}
```

## 전체 구성

다음은 `metadata` 블록 내에서 사용할 수 있는 모든 구성 옵션을 보여주는 예제입니다.

```kotlin
ingress {
    metadata("full-metadata-example") {
        namespace = "production"
        generateName = "app-prefix-"
        
        labels {
            label("app", "my-app")
            label("env", "prod")
        }
        
        annotations {
            annotation("cert-manager.io/cluster-issuer", "letsencrypt-prod")
            annotation("description", "Managed by Kube KTS")
        }
        
        finalizers {
            finalizer("kubernetes.io/pvc-protection")
        }
        
        ownerReferences {
            ownerReference("apps/v1", "Deployment", "parent-deploy", UUID.fromString("550e8400-e29b-11d4-a716-446655440000")) {
                controller = true
                blockOwnerDeletion = true
            }
        }
    }
    // ... remaining ingress configuration
}
```

## 구성 참조

### 속성

| 속성 | 타입 | 설명 |
| :--- | :--- | :--- |
| `name` | `String` | 리소스의 이름(`metadata` 함수의 첫 번째 인수로 전달됨). 네임스페이스 내에서 고유해야 합니다. |
| `namespace` | `String?` | 리소스가 생성될 네임스페이스. 기본값은 `default`. |
| `generateName` | `String?` | 이름의 접두사. Kubernetes는 접미사를 추가하여 고유한 이름을 생성합니다. |

### 메서드

| 메서드 | 설명 |
| :--- | :--- |
| `labels { label(key, value) }` | 정리 및 선택을 위한 레이블을 추가합니다.(대안: `addLabel`) |
| `annotations { annotation(key, value) }` | 도구가 사용할 수 있는 비식별 메타데이터를 추가합니다.(대안: `addAnnotation`) |
| `finalizers { finalizer(name) }` | 리소스의 삭제 수명 주기를 제어하는 파이널라이저를 추가합니다.(대안: `addFinalizer`, `addFinalizers`) |
| `ownerReferences { ownerReference(...) { ... } }` | 다른 리소스에 대한 의존성(소유자 관계)을 정의합니다.(대안: `addOwnerReference`) |

### 소유자 참조(`ownerReference`)

`ownerReferences` 내에서 다음 필드를 설정할 수 있습니다.

| 속성 | 타입 | 설명 |
| :--- | :--- | :--- |
| `apiVersion` | `String` | 소유 객체의 API 버전. |
| `kind` | `String` | 소유 객체의 종류. |
| `name` | `String` | 소유 객체의 이름. |
| `uid` | `UUID` | 소유 객체의 고유 ID(UID). |
| `controller` | `Boolean?` | 이 참조가 객체를 관리하는 컨트롤러를 가리키는지 여부. |
| `blockOwnerDeletion` | `Boolean?` | 이 객체가 존재하는 동안 소유자 삭제를 차단할지 여부. |

## 메타데이터 정보

메타데이터는 다음과 같은 이유로 Kubernetes에 필수적입니다.

1. **리소스 식별**: `name`과 `namespace`를 통해.
2. **리소스 그룹화**: `labels`를 통해 리소스를 효율적으로 필터링하고 선택할 수 있습니다(예: Service 또는 NetworkPolicy에 의해).
3. **확장성 지원**: `annotations`를 통해 컨트롤러나 외부 도구를 위한 추가 정보를 저장할 수 있습니다.
4. **수명 주기 관리**: `finalizers`와 `ownerReferences`를 통해 리소스가 올바른 순서로 깔끔하게 삭제됩니다.

Kube KTS에서는 유효한 Kubernetes 리소스를 생성하려면 모든 템플릿(`service`, `ingress` 등)에 메타데이터를 설정해야 합니다.
