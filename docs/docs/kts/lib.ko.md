# 라이브러리 파일(`*.lib.kts`)

라이브러리 파일을 사용하면 재사용 가능한 헬퍼 함수와 상수를 정의할 수 있으며, 이는 Helm 저장소의 모든 spec 파일에서 자동으로 사용할 수 있습니다.

---

## 개요

| 속성 | 값 |
| :--- | :--- |
| 파일 확장자 | `*.lib.kts` |
| 위치 | `helm/` 디렉터리 트리 내 어디든 |
| YAML로 렌더링 | 아니요 |
| spec 파일에서 사용 가능 | 예 —— 모든 spec 파일에서 자동으로 |
| 다른 라이브러리 파일에서 사용 가능 | 아니요 |

---

## 라이브러리 파일 만들기

`helm/` 디렉터리 내 어디든 `.lib.kts` 확장자를 가진 파일을 만드세요.
공유 헬퍼를 `templates/helpers.lib.kts`에 두는 것이 일반적인 관례입니다.

```
─ helm
  ├── Chart.spec.kts
  ├── values.yaml
  └── templates
      ├── helpers.lib.kts       ← library file
      ├── deployment.spec.kts
      └── service.spec.kts
```

파일 안에서 필요한 Kotlin 함수, 상수 또는 확장 함수를 정의하세요.

```kotlin
// templates/helpers.lib.kts

fun appLabels(name: String): Map<String, String> = mapOf(
    "app.kubernetes.io/name" to name,
    "app.kubernetes.io/managed-by" to "kube-kts"
)

fun fullName(release: String, component: String) = "$release-$component"

const val DEFAULT_REGISTRY = "registry.example.com"
```

---

## spec 파일에서 라이브러리 함수 사용

임의의 `*.lib.kts` 파일에 정의된 모든 함수와 상수는 모든 `*.spec.kts` 파일에서 직접 호출할 수 있습니다 —— 임포트가 필요 없습니다.

```kotlin
// templates/deployment.spec.kts

deployment {
    metadata(fullName("my-release", "backend")) {
        appLabels("backend").forEach { label(it.key, it.value) }
    }
    spec {
        template {
            spec {
                container("backend") {
                    image = "$DEFAULT_REGISTRY/backend:latest"
                }
            }
        }
    }
}
```

---

## 기본 임포트

라이브러리 파일은 spec 파일과 동일한 기본 임포트에 접근할 수 있으므로, 명시적인
import 문 없이 모든 Kube KTS DSL 타입, Java 표준 라이브러리 타입, Kotlin 시간 확장을 사용할 수 있습니다.

| 임포트 | 포함 항목 |
| :--- | :--- |
| Kube KTS API | 모든 `*Spec` 및 `*SpecBuilder` 타입, `ValueAccess` |
| `java.net` | `URL`, `URI` |
| `java.time` | `Duration`, `Instant`, `LocalDate`, … |
| `kotlin.time` | `Duration`, `Duration.Companion.*` |

---

## 제약 사항

!!! warning "보안: 임포트 제한"
    기본적으로 KTS 스크립트는 `import` 문이나 완전 정규화된 클래스 이름
    (예: `java.lang.Runtime`)을 **허용하지 않습니다**. 위에 나열된 사전 구성된 기본 임포트의 타입만 사용할 수 있습니다.

    `--unsafe` 플래그를 사용하면 이러한 제한을 해제할 수 있습니다.

!!! warning "라이브러리 파일은 다른 라이브러리 파일에 접근할 수 없습니다"
    하나의 `*.lib.kts` 파일에 정의된 함수는 다른 `*.lib.kts` 파일에서 **사용할 수 없습니다**.
    라이브러리 함수를 호출할 수 있는 것은 `*.spec.kts` 파일뿐입니다. 그에 맞게 라이브러리를 구성하고 라이브러리 간 의존성을 피하세요.

!!! note "라이브러리 파일은 렌더링되지 않습니다"
    `*.lib.kts` 파일은 최상위 구문을 포함하더라도 YAML 출력 파일로 렌더링되지 않습니다.
    출력을 생성하는 것은 `*.spec.kts` 파일뿐입니다.

---

## 여러 라이브러리 파일

`*.lib.kts` 파일은 원하는 만큼 만들 수 있습니다. Kube KTS는 `helm/` 디렉터리 트리 내에서
이들을 모두 재귀적으로 발견하여 모든 spec 파일에서 사용할 수 있게 합니다.

```
─ helm
  └── templates
      ├── helpers.lib.kts         ← general helpers
      ├── labels.lib.kts          ← label utilities
      ├── resources.lib.kts       ← resource limit constants
      └── deployment.spec.kts     ← can call functions from all three lib files
```
