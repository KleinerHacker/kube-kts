# Job DSL

`job` DSL은 Kubernetes **Job** 리소스를 구성하는 데 사용됩니다. Job은 하나 이상의 Pod를 생성하여
완료될 때까지 실행합니다. [Deployment](deployment.md)가 관리하는 장기 실행 서비스와 달리 배치 및
완료 실행형 워크로드에 적합합니다.

!!! warning "보안: 임포트 제한"
    기본적으로 KTS 스크립트는 `import` 문이나 정규화된 클래스 이름(예: `java.lang.Runtime`)을
    **허용하지 않습니다**. 사전 구성된 기본 임포트를 통해 제공되는 타입만 사용할 수 있습니다.

    `--unsafe` 플래그를 사용하면 이러한 제한을 해제할 수 있습니다.

## 기본 사용법

최소한의 Job에는 `metadata`와 `spec` 내의 Pod `template`이 필요합니다. Pod의 `restartPolicy`는
`Never` 또는 `OnFailure`여야 합니다.

```kotlin
job {
    metadata("my-job") {
        namespace = "default"
    }

    spec {
        template {
            spec {
                containers {
                    container("worker", "busybox") { }
                }
                restartPolicy = PodSpec.RestartPolicy.Never
            }
        }
    }
}
```

## 상세 예제

다음은 병렬성, 완료 수, 재시도 처리, 자동 정리, 명시적 셀렉터, Pod 실패 정책 및 성공 정책을 보여주는
종합 예제입니다.

```kotlin
job {
    metadata("full-job") {
        namespace = "batch"
    }

    spec {
        parallelism = 2
        completions = 5
        completionMode = JobSpec.CompletionMode.Indexed
        backoffLimit = 4
        backoffLimitPerIndex = 2
        maxFailedIndexes = 3
        activeDeadlineSeconds = 600.seconds.toJavaDuration()
        ttlSecondsAfterFinished = 3600.seconds.toJavaDuration()
        suspend = false
        manualSelector = true
        podReplacementPolicy = JobSpec.PodReplacementPolicy.Failed

        // 공유 레이블 셀렉터 DSL 재사용(Deployment와 동일)
        selector {
            matchLabels {
                label("app", "demo")
            }
        }

        // 특정 Pod 실패에 대응
        podFailurePolicy {
            rule(PodFailurePolicySpec.Action.FailJob) {
                onExitCodes(PodFailurePolicySpec.OnExitCodes.Operator.In) {
                    containerName = "worker"
                    values(1, 42)
                }
            }
            rule(PodFailurePolicySpec.Action.Ignore) {
                onPodCondition("DisruptionTarget", "True")
            }
        }

        // 인덱스 Job을 조기에 성공으로 선언
        successPolicy {
            rule {
                succeededIndexes = "0-2"
                succeededCount = 2
            }
        }

        template {
            metadata {
                labels {
                    label("app", "demo")
                }
            }

            spec {
                containers {
                    container("worker", "busybox") { }
                }
                restartPolicy = PodSpec.RestartPolicy.OnFailure
            }
        }
    }
}
```

## 구성 참조

### 메타데이터(`metadata`)

| 속성 | 타입 | 설명 |
| :--- | :--- | :--- |
| `name` | `String` | Job 리소스의 이름(첫 번째 인수로 전달). |
| `namespace` | `String?` | 리소스의 네임스페이스. |
| `generateName` | `String?` | 고유한 이름을 생성하기 위한 선택적 접두사. |

### Job 명세(`spec`)

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `parallelism` | 병렬로 실행해야 하는 최대 Pod 수. |
| `completions` | 성공적으로 완료되어야 하는 Pod 수. |
| `completionMode` | `NonIndexed` 또는 `Indexed`. |
| `backoffLimit` | Job이 실패로 표시되기 전의 재시도 횟수. |
| `backoffLimitPerIndex` | 인덱스당 재시도 횟수(인덱스 Job 전용). |
| `maxFailedIndexes` | Job이 실패하기 전 허용되는 최대 실패 인덱스 수(인덱스 Job 전용). |
| `activeDeadlineSeconds` | Job이 종료되기 전 활성 상태로 있을 수 있는 최대 시간. |
| `ttlSecondsAfterFinished` | Job 완료 후 TTL. 이를 초과하면 정리 대상이 됩니다. |
| `suspend` | true이면 Job 컨트롤러가 Pod를 생성하지 않습니다. |
| `manualSelector` | true이면 `selector`를 시스템이 아닌 사용자가 관리합니다. |
| `podReplacementPolicy` | `TerminatingOrFailed` 또는 `Failed`. |
| `selector { ... }` | 레이블 셀렉터(공유 셀렉터 DSL 재사용, [셀렉터](deployment/selector.md) 참조). |
| `podFailurePolicy { rule(action) { ... } }` | Job이 Pod 실패에 어떻게 대응할지 제어하는 규칙. |
| `successPolicy { rule { ... } }` | 인덱스 Job을 언제 성공으로 선언할지 정의하는 규칙. |
| `template { ... }` | Pod 템플릿([Pod 템플릿](deployment/template.md) 참조). |

### Pod 실패 정책(`podFailurePolicy`)

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `rule(action) { ... }` | 규칙을 추가합니다. `action`은 `FailJob`, `Ignore`, `Count`, `FailIndex`. |
| `onExitCodes(operator) { containerName; values(...) }` | 컨테이너 종료 코드 매칭(`operator`: `In`/`NotIn`). |
| `onPodCondition(type, status)` | Pod 조건 매칭(예: `"DisruptionTarget", "True"`). |

### 성공 정책(`successPolicy`)

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `rule { ... }` | 규칙을 추가합니다. |
| `succeededIndexes` | 성공해야 하는 인덱스 집합(예: `"0-2"`). |
| `succeededCount` | 성공한 인덱스의 최소 수. |

!!! note "restartPolicy"
    Deployment와 달리 Job의 Pod 템플릿은 `restartPolicy`를 `Never` 또는 `OnFailure`로 설정해야
    **합니다**. Job에서는 `Always`를 사용할 수 없습니다.
