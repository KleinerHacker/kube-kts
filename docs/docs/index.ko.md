# 개요

Kube KTS는 Kubernetes를 위한 Helm 래퍼이며 **기존 Helm과 완전히 호환됩니다**.
Go 템플릿이 포함된 YAML 파일 대신 **Kotlin Script(KTS)**를 사용하여 Kubernetes 리소스를 정의합니다.

이를 통해 다음과 같은 최신 언어 기능을 사용할 수 있습니다.

- 타입 안전성
- 널 안전성
- 함수형 프로그래밍 구문

또한 다음과 같은 이점도 누릴 수 있습니다.

- 완전한 IDE 지원(예: IntelliJ IDEA, VS Code)
- 디버깅 기능
- 템플릿 기반 방식보다 향상된 개발자 경험

---

# 시작하기

Kube KTS를 설치하고 `PATH`에 추가하면 곧바로 Kubernetes 리소스 개발을 시작할 수 있습니다.

Kube KTS는 **표준 Helm 저장소 구조**에서 동작합니다.
올바른 IDE 지원을 위해 모든 `.kts` 파일은 `helm` 디렉터리 안에 있어야 합니다.

---

## 프로젝트 구조

프로젝트 구조는 기존 Helm 저장소와 동일합니다.

```
─ helm
  ├── Chart.spec.kts
  ├── values.yaml
  └── templates
      ├── deployment.spec.kts
      ├── service.spec.kts
      ├── helpers.lib.kts
      └── ...
```

처리 과정에서 Kube KTS는 Helm 호환 출력을 생성합니다.

```
─ helm
  ├── Chart.yaml
  ├── values.yaml
  └── templates
      ├── deployment.yaml
      ├── service.yaml
      └── ...
```

!!! note "라이브러리 파일은 렌더링되지 않습니다"
    `.lib.kts` 확장자를 가진 파일은 YAML로 렌더링되지 않습니다. 해당 내용은
    컴파일 시점에 모든 spec 파일에서 자동으로 사용할 수 있게 됩니다.

---

## KTS 파일 유형

Kube KTS는 두 가지 유형의 Kotlin Script 파일을 구분합니다.

| 확장자 | 용도 |
| :--- | :--- |
| `*.spec.kts` | Kubernetes 리소스를 정의합니다(YAML로 렌더링됨) |
| `*.lib.kts` | 모든 spec 파일에서 사용할 수 있는 헬퍼 함수를 정의합니다 |

### Spec 파일(`*.spec.kts`)

Spec 파일은 KTS DSL을 사용하여 Kubernetes 리소스를 정의합니다. 각 spec 파일은
하나의 YAML 출력 파일을 생성합니다. `Chart.spec.kts`는 `Chart.yaml`을 생성하는 특수한
spec 파일입니다.

```kotlin
// templates/deployment.spec.kts
deployment {
    metadata("my-app") { }
    spec { /* ... */ }
}
```

!!! warning "보안: 임포트 제한"
    기본적으로 KTS 스크립트는 `import` 문이나 완전 정규화된 클래스 이름
    (예: `java.lang.Runtime`)을 **허용하지 않습니다**. 사전 구성된 기본 임포트로
    제공되는 타입만 사용할 수 있습니다 —— 전체 목록은 [기본 임포트](kts/lib.md#default-imports)를 참조하세요.

    `--unsafe` 플래그를 사용하면 이러한 제한을 해제할 수 있습니다.

### 라이브러리 파일(`*.lib.kts`)

라이브러리 파일은 재사용 가능한 헬퍼 함수를 정의하며, 이 함수들은 동일한 저장소 내의
모든 spec 파일에서 자동으로 사용할 수 있습니다. 라이브러리 파일은 YAML 출력으로
**렌더링되지 않으며**, 다른 라이브러리 파일에서 **접근할 수 없습니다**.

```kotlin
// templates/helpers.lib.kts
fun appLabels(name: String) = mapOf("app" to name, "managed-by" to "kube-kts")
```

```kotlin
// templates/deployment.spec.kts —— appLabels()를 직접 호출할 수 있습니다
deployment {
    metadata("my-app") {
        labels { appLabels("my-app").forEach { label(it.key, it.value) } }
    }
}
```

---

## 기존 호환성

Kube KTS는 기존 Helm 구성과 완전히 호환됩니다.

- 기존 YAML 파일을 `.spec.kts` 파일과 함께 사용할 수 있습니다
- 일반 `.kts` 파일(`.spec.` 한정자 없음)도 기존 spec 파일로 지원됩니다
- 기존 파일은 최종 출력으로 **변경 없이 복사**됩니다
- 혼합 환경(YAML + KTS)도 완전히 지원됩니다

---

## 값 파일

Kube KTS는 Helm 스타일의 값 파일을 지원합니다.

- 값은 `values.yaml`에 정의할 수 있습니다
- 오버라이드 동작은 표준 Helm과 동일합니다
- 값은 KTS 스크립트 내에서 접근할 수 있습니다

---

## Kube KTS 실행

Kube KTS는 Helm 저장소를 처리하는 명령줄 도구입니다.

다음과 같이 실행합니다.
`kube-kts`
저장소를 처리하려면 Helm 프로젝트 경로를 제공하세요.
자세한 사용법은 **"Kube KTS CLI 사용법"**을 참조하세요.
