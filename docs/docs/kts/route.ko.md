# Route DSL

`route` DSL은 서비스를 외부 호스트 이름으로 노출하고 필요에 따라 OpenShift 라우터에서 TLS를 종료하는
**OpenShift Route** 리소스를 구성하는 데 사용됩니다.

!!! danger "표준 Kubernetes 리소스가 아닙니다"
    `Route`(`apiVersion: route.openshift.io/v1`)는 표준 Kubernetes의 일부가 **아닙니다**. 이는
    **OpenShift / OKD**(또는 OpenShift 라우터를 포함하는 호환 배포판)에 특화된 리소스이며,
    바닐라 Kubernetes 클러스터에서는 작동하지 않습니다.

    표준 Kubernetes에서는 [Ingress DSL](ingress.md)을 대신 사용하세요.

!!! warning "보안: 임포트 제한"
    기본적으로 KTS 스크립트는 `import` 문이나 정규화된 클래스 이름(예: `java.lang.Runtime`)을
    **허용하지 않습니다**. 사전 구성된 기본 임포트를 통해 제공되는 타입만 사용할 수 있습니다.

    `--unsafe` 플래그를 사용하면 이러한 제한을 해제할 수 있습니다.

## 기본 사용법

최소한의 Route에는 `metadata`와 `spec` 내의 백엔드(`to`)가 필요합니다.

```kotlin
route {
    metadata("my-route") {
        namespace = "default"
    }

    spec {
        host = "www.example.com"
        to("my-service") {
            weight = 100
        }
        port(8080)
    }
}
```

## 상세 예제

다음은 경로 기반 라우팅, 대체 백엔드를 통한 가중치 트래픽 분할, edge TLS 종료를 보여주는 종합 예제입니다.

```kotlin
route {
    metadata("full-route") {
        namespace = "production"
    }

    spec {
        host = "www.example.com"
        path = "/app"

        // 기본 백엔드
        to("main-service") {
            weight = 100
        }

        // 가중치 트래픽 분할(예: 카나리 배포)
        alternateBackends {
            backend("canary-service") {
                weight = 20
            }
        }

        // 백엔드 서비스의 대상 포트(숫자 또는 이름)
        port(8080)

        // Edge TLS 종료, 안전하지 않은 트래픽을 HTTPS로 리디렉션
        tls(RouteTlsSpec.Termination.Edge) {
            insecureEdgeTerminationPolicy = RouteTlsSpec.InsecureEdgeTerminationPolicy.Redirect
            key = "<PEM private key>"
            certificate = "<PEM certificate>"
            caCertificate = "<PEM CA certificate>"
        }

        wildcardPolicy = RouteSpec.WildcardPolicy.None
    }
}
```

## 구성 참조

### 메타데이터(`metadata`)

| 속성 | 타입 | 설명 |
| :--- | :--- | :--- |
| `name` | `String` | Route 리소스의 이름(첫 번째 인수로 전달). |
| `namespace` | `String?` | 리소스의 네임스페이스. |
| `generateName` | `String?` | 고유한 이름을 생성하기 위한 선택적 접두사. |

### Route 명세(`spec`)

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `host` | Route가 게시되는 외부에서 접근 가능한 호스트 이름. |
| `path` | 경로 기반 라우팅을 위한 선택적 경로. |
| `to(name) { ... }` | Route가 트래픽을 전달하는 기본 백엔드(Service). |
| `addAlternateBackend(name) { ... }` | 가중치 분할을 위해 단일 대체 백엔드를 추가합니다. |
| `alternateBackends { backend(name) { ... } }` | 여러 대체 백엔드를 추가합니다. |
| `port(Int)` / `port(String)` | 백엔드 서비스의 대상 포트(숫자 또는 이름). |
| `tls(termination) { ... }` | TLS 종료를 구성합니다. |
| `wildcardPolicy` | `None` 또는 `Subdomain`. |

### 백엔드(`to` / `backend`)

| 속성 | 설명 |
| :--- | :--- |
| `kind` | 참조되는 객체의 종류. 기본값은 `Service`. |
| `weight` | 가중치 트래픽 분할을 위한 선택적 상대 가중치(0-256). |

### TLS 구성(`tls`)

| 속성 | 설명 |
| :--- | :--- |
| `termination` | TLS 종료 유형: `Edge`, `Passthrough`, `Reencrypt`. |
| `insecureEdgeTerminationPolicy` | 안전하지 않은 HTTP 트래픽 동작: `None`, `Allow`, `Redirect`. |
| `key` | PEM 인코딩된 개인 키(edge/reencrypt). |
| `certificate` | PEM 인코딩된 인증서(edge/reencrypt). |
| `caCertificate` | PEM 인코딩된 CA 인증서 체인(edge/reencrypt). |
| `destinationCACertificate` | 백엔드를 검증하는 PEM 인코딩된 CA 인증서(reencrypt 전용). |
