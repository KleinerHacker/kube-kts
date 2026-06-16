# Ingress DSL

`ingress` DSL은 일반적으로 HTTP를 통해 클러스터 내 서비스에 대한 외부 접근을 관리하는 Kubernetes Ingress 리소스를 구성하는 데 사용됩니다.

!!! warning "보안: 임포트 제한"
    기본적으로 KTS 스크립트는 `import` 문이나 완전 정규화된 클래스 이름
    (예: `java.lang.Runtime`)을 **허용하지 않습니다**. 사전 구성된 기본 임포트로
    제공되는 타입만 사용할 수 있습니다.

    `--unsafe` 플래그를 사용하면 이러한 제한을 해제할 수 있습니다.

## 기본 사용법

최소한의 Ingress 구성에는 `metadata`와 `spec` 내의 규칙이 최소 하나 필요합니다.

```kotlin
ingress {
    metadata("my-ingress") {
        namespace = "default"
    }

    spec {
        rules {
            rule {
                host = "example.com"
                httpPaths {
                    httpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
                        path = "/"
                        serviceBackend("my-service") {
                            port(80)
                        }
                    }
                }
            }
        }
    }
}
```

## 상세 예제

다음은 TLS와 여러 규칙을 포함하여 다양한 구성 옵션을 보여주는 종합 예제입니다.

```kotlin
ingress {
    metadata("full-ingress") {
        namespace = "production"
    }

    spec {
        ingressClassName = "nginx"

        // Default backend when no rules match
        defaultServiceBackend("default-service") {
            port(8080)
        }

        // TLS configuration
        tlsList {
            tls {
                secretName = "example-tls-secret"
                hosts {
                    host("example.com")
                    host("api.example.com")
                }
            }
        }

        // Rule for the main website
        rules {
            rule {
                host = "example.com"
                httpPaths {
                    httpPath(RulesSpec.HttpPathConfig.PathType.Exact) {
                        path = "/home"
                        serviceBackend("web-service") {
                            port("http") // Reference port by name
                        }
                    }
                }
            }
        }

        // Rule for API with multiple paths
        rules {
            rule {
                host = "api.example.com"
                httpPaths {
                    httpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
                        path = "/v1"
                        serviceBackend("api-v1-service") {
                            port(8081)
                        }
                    }
                    httpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
                        path = "/v2"
                        serviceBackend("api-v2-service") {
                            port(8082)
                        }
                    }
                }
            }
        }
    }
}
```

## 구성 참조

### 메타데이터(`metadata`)

| 속성 | 타입 | 설명 |
| :--- | :--- | :--- |
| `name` | `String` | Ingress 리소스의 이름(첫 번째 인수로 전달됨). |
| `namespace` | `String?` | 리소스의 네임스페이스. |
| `generateName` | `String?` | 고유한 이름 생성을 위한 선택적 접두사. |

### Ingress 명세(`spec`)

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `ingressClassName` | IngressClass 클러스터 리소스의 이름. |
| `defaultServiceBackend(name) { ... }` | 서비스의 기본 백엔드를 설정합니다. |
| `defaultResourceBackend(name, kind) { ... }` | 사용자 정의 리소스의 기본 백엔드를 설정합니다. |
| `tlsList { tls { ... } }` | TLS 구성 블록을 추가합니다.(대안: `addTls`) |
| `rules { rule { ... } }` | Ingress 규칙을 추가합니다.(대안: `addRule`) |

### TLS 구성(`tls`)

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `secretName` | TLS 인증서와 키를 포함하는 Secret의 이름. |
| `hosts { host(String) }` | TLS 인증서에 포함할 호스트를 추가합니다.(대안: `addHost`) |

### 규칙(`rule`)

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `host` | 네트워크 호스트의 완전 정규화된 도메인 이름. |
| `httpPaths { httpPath(type) { ... } }` | 규칙에 HTTP 경로를 추가합니다.(대안: `addHttpPath`) |

### HTTP 경로(`httpPath`)

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `path` | 들어오는 요청의 경로와 일치시키는 경로. |
| `type` | 경로 일치 유형: `Exact`, `Prefix` 또는 `ImplementationSpecific`. |
| `serviceBackend(name) { ... }` | Service 백엔드를 참조합니다. |
| `resourceBackend(name, kind) { ... }` | 사용자 정의 리소스 백엔드를 참조합니다. |

### 백엔드 구성

`serviceBackend`를 사용할 때는 포트를 지정해야 합니다.
- `port(Int)`: 숫자 포트를 사용합니다.
- `port(String)`: 명명된 포트를 사용합니다.
