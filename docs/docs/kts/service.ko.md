# Service DSL

`service` DSL은 Pod의 논리적 집합과 이에 접근하기 위한 정책을 정의하는 Kubernetes Service 리소스를 구성하는 데 사용됩니다.

!!! warning "보안: 임포트 제한"
    기본적으로 KTS 스크립트는 `import` 문이나 완전 정규화된 클래스 이름
    (예: `java.lang.Runtime`)을 **허용하지 않습니다**. 사전 구성된 기본 임포트로
    제공되는 타입만 사용할 수 있습니다.

    `--unsafe` 플래그를 사용하면 이러한 제한을 해제할 수 있습니다.

## 기본 사용법

최소한의 Service 구성에는 `metadata`와 `spec` 내의 포트가 최소 하나 필요합니다.

```kotlin
service {
    metadata("my-service") {
        namespace = "default"
    }

    spec {
        type = ServiceSpec.Type.ClusterIP
        ports {
            port("http") {
                port = 80
                targetPort = 8080
            }
        }
    }
}
```

## 상세 예제

다음은 Service의 다양한 구성 옵션을 보여주는 종합 예제입니다.

```kotlin
service {
    metadata("full-service") {
        namespace = "production"
    }

    spec {
        type = ServiceSpec.Type.LoadBalancer
        
        ports {
            port("https") {
                port = 443
                targetPort = 8443
                protocol = PortMappingSpec.Protocol.TCP
                appProtocol = "https"
            }
        }

        // IP configuration
        clusterIPs {
            clusterIP("10.0.0.1")
        }
        ipFamilies {
            ipFamily(ServiceSpec.IPFamily.IPv4)
        }
        ipFamilyPolicy = ServiceSpec.FamilyPolicy.SingleStack

        // External access
        externalIPs {
            externalIP("1.2.3.4")
        }
        externalTrafficPolicy = ServiceSpec.TrafficPolicy.Local
        
        // Load balancer settings
        allocateLoadBalancerNodePorts = true
        loadBalancerClass = "example.com/internal-lb"
        loadBalancerSourceRanges {
            loadBalancerSourceRange("192.168.0.0/24")
        }

        // Session affinity
        sessionAffinity = ServiceSpec.SessionAffinity.ClientIP
        sessionAffinityClientTimeout = 60.seconds.toJavaDuration()

        // Traffic management
        publishNotReadyAddresses = false
        trafficDistribution = ServiceSpec.TrafficDistribution.PreferClose
    }
}
```

## 구성 참조

### 메타데이터(`metadata`)

| 속성 | 타입 | 설명 |
| :--- | :--- | :--- |
| `name` | `String` | Service의 이름(첫 번째 인수로 전달됨). |
| `namespace` | `String?` | 리소스의 네임스페이스. |
| `generateName` | `String?` | 고유한 이름 생성을 위한 선택적 접두사. |

### Service 명세(`spec`)

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `type` | Service의 유형(예: `ClusterIP`, `LoadBalancer`). |
| `ports { port(name) { ... } }` | 포트 매핑 구성을 추가합니다.(대안: `addPort`) |
| `clusterIPs { clusterIP(ip) }` | 클러스터 IP 주소를 설정합니다.(대안: `addClusterIP`, `addClusterIPs`) |
| `ipFamilies { ipFamily(family) }` | IP 패밀리(IPv4/IPv6)를 추가합니다.(대안: `addIpFamily`, `addIpFamilies`) |
| `ipFamilyPolicy` | IP 패밀리 정책을 설정합니다. |
| `externalIPs { externalIP(ip) }` | 외부 IP 주소를 추가합니다.(대안: `addExternalIP`, `addExternalIPs`) |
| `externalName` | `ExternalName` 유형 Service의 외부 이름. |
| `externalTrafficPolicy` | 외부 트래픽에 대한 트래픽 정책. |
| `internalTrafficPolicy` | 내부 트래픽에 대한 트래픽 정책. |
| `allocateLoadBalancerNodePorts` | LoadBalancer 유형 Service에 노드 포트를 할당할지 여부. |
| `loadBalancerIP` | **사용 중단됨.** 대신 구현별 어노테이션을 사용하세요. |
| `loadBalancerClass` | 로드 밸런서의 클래스. |
| `loadBalancerSourceRanges { loadBalancerSourceRange(range) }` | 로드 밸런서에 허용되는 CIDR 범위를 추가합니다.(대안: `addLoadBalancerSourceRange`) |
| `sessionAffinity` | 세션 어피니티 정책. |
| `sessionAffinityClientTimeout` | 클라이언트 IP 기반 세션 어피니티의 타임아웃. |
| `publishNotReadyAddresses` | 준비되지 않은 Pod의 주소를 게시할지 여부. |
| `healthCheckNodePort` | 상태 확인용 포트(Type=LoadBalancer의 경우). |
| `trafficDistribution` | 트래픽 분산 정책. |

### 포트 매핑(`port`)

| 속성 | 타입 | 설명 |
| :--- | :--- | :--- |
| `port` | `Int` | Service가 노출하는 포트. |
| `targetPort` | `Int?` | Service가 트래픽을 전달하는 Pod의 포트. |
| `nodePort` | `Int?` | 각 노드에서 Service가 노출되는 포트. |
| `protocol` | `Protocol?` | IP 프로토콜(TCP, UDP, SCTP). |
| `appProtocol` | `String?` | 애플리케이션 프로토콜(예: http, https). |

## 특수 타입

- `ServiceSpec.Type`: `ClusterIP`, `NodePort`, `LoadBalancer`, `ExternalName`.
- `PortMappingSpec.Protocol`: `TCP`, `UDP`, `SCTP`.
- `ServiceSpec.IPFamily`: `IPv4`, `IPv6`.
- `ServiceSpec.FamilyPolicy`: `SingleStack`, `PreferDualStack`, `RequireDualStack`.
- `ServiceSpec.TrafficPolicy`: `Cluster`, `Local`.
- `ServiceSpec.SessionAffinity`: `None`, `ClientIP`.
- `ServiceSpec.TrafficDistribution`: `PreferClose`.
