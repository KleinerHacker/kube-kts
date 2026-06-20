# StatefulSet DSL

`statefulSet` DSL은 Kubernetes **StatefulSet** 리소스를 구성하는 데 사용됩니다. StatefulSet은 안정적이고
고유한 네트워크 식별자와 Pod별 안정적인 영구 스토리지를 갖는 Pod 집합을 관리합니다.
[Deployment](deployment.md)가 관리하는 교체 가능한 Pod와 달리, 데이터베이스나 메시지 브로커와 같은
스테이트풀 애플리케이션에 적합합니다.

!!! warning "보안: 임포트 제한"
    기본적으로 KTS 스크립트는 `import` 문이나 완전한 클래스 이름(예: `java.lang.Runtime`)을
    **허용하지 않습니다**. 사전 구성된 기본 임포트로 제공되는 타입만 사용할 수 있습니다.

    이러한 제한을 해제하려면 `--unsafe` 플래그를 사용하세요.

## 기본 사용법

최소한의 StatefulSet에는 `metadata`, `serviceName`(네트워크 도메인을 제어하는 헤드리스 Service),
`selector`, 그리고 `spec` 안의 Pod `template`이 필요합니다.

```kotlin
statefulSet {
    metadata("my-database") {
        namespace = "default"
    }

    spec {
        serviceName = "my-database"

        selector {
            matchLabels {
                label("app", "my-database")
            }
        }

        template {
            metadata {
                labels {
                    label("app", "my-database")
                }
            }

            spec {
                containers {
                    container("db", "postgres") { }
                }
            }
        }
    }
}
```

## 상세 예제

다음은 레플리카 수, 제어 Service, Pod 관리 정책, 업데이트 전략, `volumeClaimTemplates`를 통한 Pod별 영구
스토리지, PVC 보존 정책을 보여주는 종합 예제입니다.

```kotlin
statefulSet {
    metadata("full-database") {
        namespace = "data"
    }

    spec {
        replicas = 3
        serviceName = "full-database"
        podManagementPolicy = StatefulSetSpec.PodManagementPolicy.Parallel
        revisionHistoryLimit = 5
        minReadySeconds = 10.seconds.toJavaDuration()
        ordinals(1)

        // 공유 라벨 셀렉터 DSL 재사용(Deployment와 동일)
        selector {
            matchLabels {
                label("app", "demo")
            }
        }

        // 템플릿 업데이트가 어떻게 롤아웃되는지 제어
        updateStrategy {
            type = StatefulSetUpdateStrategySpec.Type.RollingUpdate
            rollingUpdate {
                partition = 1
                maxUnavailable = 1.absolute
            }
        }

        // 안정적인 Pod별 스토리지: 템플릿마다 Pod당 PVC를 하나씩 프로비저닝
        volumeClaimTemplates {
            claim("data") {
                accessModes(VolumeClaimTemplateSpec.AccessMode.ReadWriteOnce)
                storageClassName = "standard"
                volumeMode = VolumeClaimTemplateSpec.VolumeMode.Filesystem
                requests {
                    storage = 1.giBytes
                }
            }
        }

        // 삭제 / 스케일 다운 시 PVC 처리 방식 결정
        persistentVolumeClaimRetentionPolicy {
            whenDeleted = PersistentVolumeClaimRetentionPolicySpec.RetentionPolicyType.Delete
            whenScaled = PersistentVolumeClaimRetentionPolicySpec.RetentionPolicyType.Retain
        }

        template {
            metadata {
                labels {
                    label("app", "demo")
                }
            }

            spec {
                containers {
                    container("db", "postgres") { }
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
| `name` | `String` | StatefulSet 리소스의 이름(첫 번째 인자로 전달). |
| `namespace` | `String?` | 리소스의 네임스페이스. |
| `generateName` | `String?` | 고유 이름 생성을 위한 선택적 접두사. |

### StatefulSet 명세(`spec`)

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `replicas` | 원하는 레플리카(Pod 인스턴스) 수. 기본값은 1. |
| `serviceName` | 네트워크 도메인을 제어하는 (보통 헤드리스) Service 이름. **필수.** |
| `podManagementPolicy` | `OrderedReady`(기본) 또는 `Parallel`. |
| `revisionHistoryLimit` | 롤백을 위해 보존할 최대 리비전 수. |
| `minReadySeconds` | 새 Pod가 사용 가능으로 간주되기 위해 Ready 상태를 유지해야 하는 최소 초. |
| `ordinals(start)` | 첫 번째 레플리카 인덱스를 나타내는 번호(기본값 0). |
| `selector { ... }` | 라벨 셀렉터(공유 셀렉터 DSL 재사용, [셀렉터](deployment/selector.md) 참조). **필수.** |
| `updateStrategy { ... }` | 템플릿 업데이트의 롤아웃 방식을 제어합니다. |
| `volumeClaimTemplates { claim(name) { ... } }` | 안정적인 Pod별 스토리지를 제공하는 PVC 템플릿. |
| `persistentVolumeClaimRetentionPolicy { ... }` | 프로비저닝된 PVC의 수명 주기를 제어합니다. |
| `template { ... }` | Pod 템플릿([Pod 템플릿](deployment/template.md) 참조). **필수.** |

### 업데이트 전략(`updateStrategy`)

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `type` | `RollingUpdate`(기본) 또는 `OnDelete`. |
| `rollingUpdate { partition; maxUnavailable }` | 롤링 업데이트 매개변수. |
| `partition` | 이 서수 이상의 Pod를 업데이트합니다(단계적 / 카나리 롤아웃). |
| `maxUnavailable` | 업데이트 중 사용 불가능한 최대 Pod 수(절대값 또는 백분율, 예: `1.absolute` / `25.percent`). |

### 볼륨 클레임 템플릿(`volumeClaimTemplates`)

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `claim(name) { ... }` | PVC 템플릿을 추가합니다. `name`은 Pod 컨테이너의 `volumeMount`와 일치해야 합니다. |
| `accessModes(...)` | 접근 모드(예: `ReadWriteOnce`, `ReadOnlyMany`, `ReadWriteMany`, `ReadWriteOncePod`). |
| `storageClassName` | 볼륨 프로비저닝에 사용할 StorageClass. |
| `volumeMode` | `Filesystem`(기본) 또는 `Block`. |
| `requests { storage = ... }` | 요청하는 최소 스토리지(예: `1.giBytes`). |
| `limits { storage = ... }` | 허용되는 최대 스토리지. |
| `label(key, value)` / `labels { ... }` | 클레임 메타데이터의 라벨. |
| `annotation(key, value)` / `annotations { ... }` | 클레임 메타데이터의 어노테이션. |

### PVC 보존 정책(`persistentVolumeClaimRetentionPolicy`)

| 속성 | 설명 |
| :--- | :--- |
| `whenDeleted` | `Retain`(기본) 또는 `Delete` —— StatefulSet이 삭제될 때 적용. |
| `whenScaled` | `Retain`(기본) 또는 `Delete` —— StatefulSet이 스케일 다운될 때 적용. |

!!! note "안정적인 스토리지"
    `volumeClaimTemplates`의 각 항목은 **Pod마다** PersistentVolumeClaim을 하나씩 프로비저닝합니다.
    클레임은 재스케줄링 후에도 동일성을 유지하여 각 레플리카에 전용 영구 스토리지를 제공합니다.
