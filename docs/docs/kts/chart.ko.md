# Chart DSL

`chart` DSL은 기존 Helm 차트의 `Chart.yaml` 파일과 마찬가지로 Helm 차트의 메타데이터와 의존성을 정의하는 데 사용됩니다.

!!! warning "보안: 임포트 제한"
    기본적으로 KTS 스크립트는 `import` 문이나 완전 정규화된 클래스 이름
    (예: `java.lang.Runtime`)을 **허용하지 않습니다**. 사전 구성된 기본 임포트로
    제공되는 타입만 사용할 수 있습니다.

    `--unsafe` 플래그를 사용하면 이러한 제한을 해제할 수 있습니다.

## 기본 사용법

차트를 정의하려면 차트의 **이름**과 **버전**을 주요 인수로 받는 `chart` 함수를 사용하세요.

```kotlin
chart("my-chart", "1.0.0") {
    description = "A short description of my chart"
    type = ChartSpec.Type.Application
}
```

## 상세 예제

다음은 `chart` 블록 내에서 사용할 수 있는 모든 구성 옵션을 보여주는 종합 예제입니다.

```kotlin
chart("full-featured-chart", "1.2.3") {
    // Metadata
    description = "A comprehensive example of the Chart DSL"
    type = ChartSpec.Type.Library // Default is Application if not specified
    home = "https://github.com/example/kube-kts"
    icon = URI("https://example.com/icon.png")
    appVersion = "2.5.0"
    deprecated = false

    // Keywords and sources
    keywords {
        keyword("kubernetes")
        keyword("kotlin")
        keyword("dsl")
    }
    sources {
        source(URI("https://github.com/example/kube-kts/src"))
    }

    // Compatibility
    kubeVersion {
        minInclusive("1.20.0")
        maxExclusive("1.30.0")
    }

    // Dependencies
    dependencies {
        dependency("common-utils", "0.5.0") {
            repository = URI("https://charts.example.com")
            alias = "utils"
            condition = "utils.enabled"

            tags {
                tag("infrastructure")
            }
            pathImportValues {
                pathImportValue("exports.values")
            }
            mappingImportValues {
                mappingImportValue("source.key", "destination.key")
            }
        }
    }

    // Maintainers
    maintainers {
        maintainer("John Doe") {
            email = MailAddress.parse("john.doe@example.com")
            url = URI("https://johndoe.com")
        }
    }

    // Custom annotations
    annotations {
        annotation("custom-metadata-key", "some-value")
    }
}
```

## 구성 참조

### 최상위 속성

| 속성 | 타입 | 설명 |
| :--- | :--- | :--- |
| `description` | `String?` | 차트의 한 줄 설명. |
| `type` | `ChartSpec.Type?` | 차트 유형: `Application` 또는 `Library`. |
| `home` | `String?` | 프로젝트 홈페이지의 URL. |
| `icon` | `URI?` | 아이콘으로 사용할 SVG 또는 PNG 이미지의 URL. |
| `appVersion` | `String?` | 이 차트에 포함된 애플리케이션의 버전(차트 버전이 아님). |
| `deprecated` | `Boolean?` | 이 차트의 사용 중단 여부. |

### 메서드

| 메서드 | 설명 |
|:-----------------------------------------------------------------| :--- |
| `keywords { ... }` | 이 차트를 찾는 데 사용되는 키워드를 추가합니다.(대안: `addKeyword`, `addKeywords`) |
| `sources { ... }` | 이 프로젝트 소스 코드의 URL을 추가합니다.(대안: `addSource`, `addSources`) |
| `kubeVersion { ... }` | 호환되는 Kubernetes 버전 범위를 설정합니다. |
| `dependencies { ... }` | 차트 의존성을 추가합니다. 아래 [의존성](#dependencies)을 참조하세요.(대안: `addDependency`) |
| `maintainers { ... }` | 관리자 정보를 추가합니다.(대안: `addMaintainer`) |
| `annotations { ... }` | 사용자 정의 어노테이션을 추가합니다.(대안: `addAnnotation`) |


### 의존성

`dependency` 블록은 세밀한 제어를 가능하게 합니다.

- `repository`: 차트 저장소의 URL.
- `alias`: 차트의 별칭(같은 차트를 여러 번 사용할 때 유용함).
- `condition`: 차트 설치 여부를 결정하는 불리언 표현식.
- `tags { tag(String) }`: 의존성을 그룹화하기 위한 태그를 추가합니다.(대안: `addTag`)
- `pathImportValues { pathImportValue(path) }`: 하위 차트에서 값을 가져옵니다.(대안: `addPathImportValue`)
- `mappingImportValues { mappingImportValue(childKey, parentKey) }`: 하위 차트의 특정 값을 상위 차트로 매핑합니다.(대안: `addMappingImportValue`)

## 특수 타입

- `ChartSpec.Type`: `Application`과 `Library` 값을 가진 열거형.
- `MailAddress`: 이메일 검증 및 형식 지정을 위한 헬퍼 타입.
- `URI`: 웹 링크를 표현하기 위한 표준 `java.net.URI`.
