# Kotlin 스크립트 (KTS)

Kube KTS 저장소는 Helm 의 Go 템플릿 YAML 대신 **Kotlin 스크립트** 로 Kubernetes 리소스를
기술합니다. 각 스크립트는 순수 Kotlin 이며, `values` 에 대해 평가되어 일반 Helm chart 로
변환된 후 Helm 이 이를 사용합니다. 결과적으로 이미 익숙한 Helm 워크플로 그대로이지만, 타입
안전성, null 안전성, 완전한 IDE 지원, 그리고 디버깅 가능하고 로직이 없는 YAML 출력이
더해집니다.

이 페이지에서는 이러한 스크립트를 어떻게 이해해야 하는지, 그리고 **safe 와 unsafe** 의 차이가
무엇인지 설명합니다. 개별 리소스(Chart, Service, Deployment 등)는 이 섹션의 전용 페이지에서
문서화되어 있습니다.

---

## KTS 저장소를 이해하는 방법

저장소는 Helm chart 를 반영하는 디렉터리 트리일 뿐입니다. Kube KTS 는 이를 스캔하여 `*.kts`
파일을 렌더링된 YAML 로 교체하고, 그 외의 모든 것은 변경 없이 복사합니다.

```
─ helm
  ├── Chart.spec.kts        → Chart.yaml
  ├── values.yaml           (그대로 복사, 스크립트에도 제공됨)
  └── templates
      ├── helpers.lib.kts   (헬퍼 함수 —— 렌더링되지 않음)
      ├── deployment.spec.kts → deployment.yaml
      └── service.spec.kts    → service.yaml
```

### 파일 유형

| 확장자 | 의미 | YAML 로 렌더링 |
| :--- | :--- | :--- |
| `*.spec.kts` | DSL 을 통해 정확히 하나의 Kubernetes 리소스를 정의(`deployment { … }`, `service { … }` 등). | 예 |
| `*.lib.kts` | 재사용 가능한 헬퍼 함수/상수를 정의하며 **모든** spec 파일에서 사용 가능. [라이브러리 파일](lib.md) 참조. | 아니오 |
| `*.kts` | 레거시 —— spec 파일로 처리됨. | 예 |
| `*.yaml` / `*.yml` | 일반 Helm YAML. 완전한 하위 호환성을 위해 유지됨. | 그대로 복사 |

### 스크립트란 실제로 무엇인가

각 `*.spec.kts` 파일은 평범한 Kotlin 스크립트입니다. 리소스를 빌드하는 단일 최상위 DSL 함수를
호출하며, 그 과정에서 chart 의 values 를 읽을 수 있습니다:

```kotlin
// templates/deployment.spec.kts
deployment {
    metadata("backend") { }
    spec {
        replicas = valueOrNull<Int>("spec.replicas") ?: 1
        template {
            spec {
                container("backend") {
                    image = value<String>("spec.image")
                }
            }
        }
    }
}
```

이것은 실행 전에 컴파일되는 진짜 Kotlin 이므로, 잘못된 타입, 속성 이름 오타, 누락된 필수 값과
같은 실수가 배포 시점에 깨진 YAML 을 생성하는 대신 **컴파일 시점**에 드러납니다. 값 접근
(`value`, `valueOrNull`, `array`, `map` 등)은 [값 처리](values.md) 에서 설명합니다.

---

## Safe 와 Unsafe

렌더링을 예측 가능하게 유지하고 스크립트가 사용자의 머신에서 할 수 있는 일을 제한하기 위해,
Kube KTS 는 기본적으로 **safe 모드** 로 스크립트를 컴파일합니다. safe 모드는 스크립트를 Kube
KTS DSL 과 엄선된 사전 임포트 타입으로 제한합니다. unsafe 모드는 이러한 제한을 해제하여
스크립트가 임의의 JVM 코드를 실행할 수 있도록 합니다.

### safe 모드가 하는 일

스크립트가 컴파일되기 전에 그 소스가 검사됩니다(문자열 리터럴과 주석이 먼저 제거되므로 규칙은
실제 코드에만 적용됩니다). 두 가지가 거부됩니다:

| safe 모드에서 거부됨 | 예시 | 이유 |
| :--- | :--- | :--- |
| `import` 문 | `import java.io.File` | 임의의 클래스 가져오기를 방지. |
| 완전 정규화된 클래스 이름 | `java.lang.Runtime.getRuntime()` | import 없이 임의의 JVM API 에 도달하는 것을 방지. |

둘 중 하나라도 발견되면 컴파일은 설명 메시지와 함께 즉시 실패합니다 —— YAML 은 생성되지 않습니다.

다만 safe 스크립트가 "비어 있는" 것은 아닙니다: 완전한 Kube KTS DSL 과 일반적인 표준 라이브러리
타입을 포함하여 풍부한 타입 집합이 **사전 임포트** 되어 있어 `import` 없이 사용할 수 있습니다:

| 사전 임포트 | 포함 항목 |
| :--- | :--- |
| Kube KTS API | 모든 `*Spec` / `*SpecBuilder` 타입, `ValueAccess`, 단위 확장(`250.mCpu`, `1.giBytes`, `25.percent` 등) |
| `java.net` | `URL`, `URI` |
| `java.time` | `Duration`, `Instant`, `LocalDate` 등 |
| `kotlin.time` | `Duration`, `Duration.Companion.*` |

대부분의 chart 에서는 이것이 필요한 전부이므로 safe 모드를 유지해야 합니다.

### unsafe 모드가 하는 일

unsafe 모드는 위의 import/FQN 검사를 단순히 **건너뜁니다**. 그러면 스크립트는 `import` 문과
완전 정규화된 클래스 이름을 사용할 수 있으며, 이는 JVM 클래스패스의 모든 클래스를 호출할 수
있음을 의미합니다 —— 파일 읽기, 프로세스 시작, 네트워크 연결 열기 등.

그 힘은 곧 위험이기도 합니다: 스크립트는 더 이상 Kubernetes 리소스 기술에 국한되지 않고,
렌더링을 실행하는 머신에서 임의의 코드를 실행할 수 있습니다. 완전히 신뢰하고 검토한 저장소에
대해서만 unsafe 모드를 활성화하십시오.

!!! warning "unsafe 모드는 임의의 코드를 실행합니다"
    unsafe 모드가 활성화되면 샌드박스가 없습니다: 악의적이거나 버그가 있는 스크립트는 현재
    사용자가 할 수 있는 모든 것을 할 수 있습니다. 활성화하는 것을 신뢰할 수 없는 코드를 실행하는
    것으로 간주하십시오.

---

## 명령줄 연산자

safe 와 unsafe 는 CLI 의 전역 **`--unsafe`** 플래그로 제어됩니다. 이 플래그가 없으면 모든
스크립트는 safe 모드로 컴파일됩니다. 이 플래그가 있으면 해당 실행의 모든 스크립트에 대해
import/FQN 제한이 해제됩니다.

```bash
# safe (기본값) —— import 와 FQN 이 거부됨
kube-kts render ./helm ./out

# unsafe —— import 와 완전 정규화된 클래스 이름이 허용됨
kube-kts --unsafe render ./helm ./out
```

`--unsafe` 는 **전역** 옵션이므로 명령 앞에 배치되며, 스크립트를 컴파일하는 모든 명령
(`validate`, `compile`, `render`, `lint`, `template`, `install` 등)에 적용됩니다. `--help`
출력과 명령 페이지에서는 보안 관련 옵션임을 나타내는 `!!!` 마커가 붙습니다. 전역 옵션의 전체
목록은 [CLI 개요](../cli/index.md#global-options) 를 참조하십시오.
