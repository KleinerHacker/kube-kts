# Pod Spec

Pod Spec은 Pod 내 모든 컨테이너의 런타임, 네트워크, 스케줄링 및 보안 옵션을 기술합니다. Deployment에서는 `template`의 일부입니다.

```kotlin
template {
    spec {
        serviceAccountName = "demo-service-account"
        restartPolicy = PodSpec.RestartPolicy.Always
        dnsPolicy = PodSpec.DnsPolicy.ClusterFirst

        nodeSelector {
            select("kubernetes.io/os", "linux")
        }

        imagePullSecrets {
            secret("registry-credentials")
        }

        containers {
            container("app", "nginx:1.27") {}
        }
    }
}
```

## 핵심 속성

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `containers { container(name, image) { ... } }` | 필수 메인 컨테이너 목록. |
| `containers { init(name, image) { ... } }` | 메인 컨테이너보다 먼저 실행되는 Init 컨테이너. |
| `containers { ephemeral(name, image) { ... } }` | 디버깅 시나리오용 임시(ephemeral) 컨테이너. |
| `restartPolicy` | 컨테이너 재시작 동작: `Always`, `OnFailure`, `Never`. |
| `serviceAccountName` | Pod 실행에 사용되는 ServiceAccount. |
| `automountServiceAccountToken` | ServiceAccount 토큰의 자동 마운트 여부를 제어합니다. |
| `imagePullSecrets { secret(name) }` | 프라이빗 컨테이너 레지스트리용 Secret. |
| `volumes { volume(name) { ... } }` | 컨테이너가 마운트할 수 있는 볼륨. |
| `nodeSelector { select(key, value) }` | 레이블을 통한 간단한 노드 선택. |
| `affinity { ... }` | 스케줄링을 위한 어피니티 및 안티어피니티 규칙. |
| `tolerations { toleration { ... } }` | 일치하는 taint가 있는 노드로의 스케줄링을 허용합니다. |
| `topologySpreadConstraints { constraint(...) { ... } }` | 토폴로지 도메인 간 Pod 분산. |
| `securityContext { ... }` | Pod 수준의 보안 컨텍스트. |
| `terminationGracePeriodSeconds` | Pod 종료를 위한 유예 기간. |
| `activeDeadlineSeconds` | Pod의 최대 수명. |

## 네트워크와 DNS

```kotlin
spec {
    dnsPolicy = PodSpec.DnsPolicy.ClusterFirst

    dnsConfig {
        nameservers {
            nameserver("10.96.0.10")
        }
        searches {
            search("svc.cluster.local")
        }
        options {
            option("ndots", "2")
        }
    }

    containers {
        container("app", "nginx:1.27") {}
    }
}
```

추가 네트워크 옵션으로는 `hostNetwork`, `hostPID`, `hostIPC`, `hostname`, `subdomain`, `setHostnameAsFQDN`가 있습니다.

## 스케줄링

```kotlin
spec {
    priorityClassName = "high-priority"
    schedulerName = "default-scheduler"

    nodeSelector {
        select("nodepool", "apps")
    }

    tolerations {
        toleration {
            key = "dedicated"
            value = "apps"
            effect = TolerationSpec.Effect.NoSchedule
        }
    }

    containers {
        container("app", "nginx:1.27") {}
    }
}
```

## 보안 컨텍스트

Pod 수준의 `securityContext { ... }`는 Pod 전체에 적용됩니다. 컨테이너는 추가로 자체 보안 컨텍스트를 정의할 수 있습니다.

```kotlin
spec {
    securityContext {
        runAsUser = 1000
        runAsGroup = 1000
        runAsNonRoot = true
    }

    containers {
        container("app", "nginx:1.27") {}
    }
}
```
