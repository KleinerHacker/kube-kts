# Kube KTS CLI

Kube KTS 는 `kube-kts` 명령으로 실행하는 명령줄 도구입니다. Helm 의 타입 안전 래퍼로서, Go 템플릿
대신 Kotlin 스크립트(KTS)로 Kubernetes 리소스를 기술하고, 이를 표준 Helm Chart 로 렌더링한 다음
실제 클러스터 작업은 Helm 에 위임합니다.

내장 `--help` 출력만으로 부족할 때 본 장이 첫 번째 참고 자료입니다. 도움말 텍스트는 화면에 들어가도록
의도적으로 짧게 유지합니다. 본 장에서는 한 걸음 더 나아가 각 옵션의 동작, 기본값, 단위, 상호작용을
자세히 설명합니다. 각 명령에는 전용 페이지가 있어 **모든** 옵션을 기재합니다.

## Helm 과의 관계

Kube KTS 는 Helm 을 대체하지 않고, Helm 의 입력을 준비합니다. 일반적인 명령은 두 단계로 실행됩니다.

1. **렌더링 단계(Kube KTS):** 저장소를 스캔하고 Kotlin 스크립트를 컴파일·실행하여 결과를 표준 Helm
   Chart 로 대상 디렉터리에 기록합니다.
2. **Helm 단계(위임):** Helm 기반 명령(`lint`, `template`, `install`, `upgrade`, `uninstall`)의 경우, Kube
   KTS 는 렌더링된 Chart 에 대해 실제 `helm` 실행 파일을 호출하고 모든 Helm 전용 옵션을 그대로
   전달합니다.

따라서 이러한 명령은 Kube KTS 옵션(렌더링에 영향)과 Helm 옵션(전달됨)을 모두 받습니다. 최종적으로
Helm 에 전달되는 옵션에는 표시가 붙습니다. 아래의 “옵션 마커 범례”를 참조하세요.

!!! note "Helm 설치 필요"
    `lint`, `template`, `install`, `upgrade`, `uninstall` 은 `helm` 실행 파일이 `PATH` 에 있어야 합니다.
    `validate`, `compile`, `render` 는 Helm 없이 동작합니다.

## 사용법

```bash
kube-kts [전역 옵션] <명령> <REPOSITORY> [TARGET] [명령 옵션]
```

### 저장소와 대상 경로

- **`REPOSITORY`** 는 첫 번째 위치 인수로, Kube KTS 저장소(`*.spec.kts`, `*.lib.kts`,
  `values.yaml` 등을 포함하는 디렉터리)를 가리킵니다. 생략하면 현재 작업 디렉터리를 사용합니다. 경로가
  존재하지 않으면 Helm 을 호출하기 전에 즉시 실패합니다.
- **`TARGET`** 은 렌더링 명령(`render`, `lint`, `template`, `install`, `upgrade`, `uninstall`)이 사용하는
  선택적 두 번째 위치 인수로, Chart 가 렌더링되는 디렉터리입니다. 생략하면 임시 디렉터리가 생성되어
  운영체제가 정리합니다. 생성된 Chart 를 확인하거나 재사용하려면 명시적으로 지정하세요.

### 종료 코드

CLI 는 성공 시 `0`, 실패 시 0 이 아닌 종료 코드를 반환합니다(잘못된 저장소, 렌더링 실패, Helm 의 0 이
아닌 종료 코드 등). 실패를 진단할 때는 `--exception` 으로 전체 스택 트레이스를 확인할 수 있습니다.

## 명령

| 명령 | 페이지 | 용도 |
|---|---|---|
| `validate` | [validate](validate.md) | KTS 저장소를 출력 없이 검증합니다. |
| `compile` | [compile](compile.md) | KTS 저장소를 객체 인스턴스로 컴파일합니다. |
| `render` | [render](render.md) | 저장소를 디스크의 표준 Helm Chart 로 렌더링합니다. |
| `lint` | [lint](lint.md) | 렌더링 후 `helm lint` 로 lint 합니다. |
| `template` | [template](template.md) | 렌더링 후 `helm template` 로 매니페스트를 출력합니다. |
| `install` | [install](install.md) | 렌더링 후 `helm install` 로 클러스터에 설치합니다. |
| `upgrade` | [upgrade](upgrade.md) | 렌더링 후 `helm upgrade` 로 릴리스를 업그레이드(또는 설치)합니다. |
| `uninstall` | [uninstall](uninstall.md) | 렌더링 후 `helm uninstall` 로 하나 이상의 릴리스를 제거합니다. |

## 이 명령들은 렌더링이 필요한가? (KTS 관련성)

명령이 KTS 저장소를 렌더링하는지 여부로, Kotlin 스크립트가 해당 명령에 관련이 있는지 — 따라서 해당
명령에 **저장소**(KTS, 순수 YAML 또는 혼합)를 전달해야 하는지 — 알 수 있습니다.

- **렌더링 필요 → 저장소 필수 (KTS 관련):** `validate`, `compile`, `render`, `lint`, `template`,
  `install`, `upgrade` 는 모두 *스캔 → 컴파일 → 렌더링* 파이프라인을 실행하므로 KTS 스크립트에 의존하며,
  저장소를 전달해야 합니다.
- **렌더링과 무관 → 저장소 불필요 (KTS 무관):** 이미 설치된 릴리스, 클러스터 또는 리포지토리에 작용하는
  작업(예: 향후 `status`, `list`, `rollback`)은 렌더링된 Chart 가 필요 없으므로 KTS 스크립트가 관여하지
  않습니다.

!!! note "`uninstall` 은 특수한 경우"
    `uninstall` 은 이름만으로 릴리스를 제거하며 기술적으로 렌더링된 Chart 가 필요 없습니다. 일관성을 위해
    현재는 Helm 호출 전에 렌더링하지만, 기능적으로 KTS 스크립트는 관련이 없습니다.

## 옵션 마커 범례

`--help` 출력에서 전용 열이 각 옵션의 성격을 표시합니다. 명령 페이지의 옵션 표에서도 동일한 마커를
사용하여 문서가 도움말 화면과 정확히 일치하도록 합니다.

| 마커 | 의미 |
|---|---|
| `---->` | 하위 Helm CLI 로 전달됩니다. 효과는 Helm 의 효과와 동일합니다. |
| `*` | 실험적이며 향후 버전에서 변경되거나 제거될 수 있습니다. `--experimental` 이 필요합니다. |
| `!!!` | 위험 / 보안 관련입니다. 사용 전에 설명을 주의 깊게 읽으세요. |

마커가 없는 옵션은 Kube KTS 내부에서만 사용되며 Helm 에 도달하지 않습니다.

## 전역 옵션

이 옵션들은 모든 명령에서 사용할 수 있으며 보통 명령 이름 **앞**에 둡니다.

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--debug` | `---->` | 어떤 단계가 실행되고 Helm 에 어떤 인수가 전달되는지를 포함한 디버그 정보를 출력합니다. Helm 에도 `--debug` 로 전달되어 Helm 자체도 상세해집니다. 예기치 않은 동작의 첫 조사 수단으로 적합합니다. |
| `--verbose` | | trace 수준의 세부 정보를 포함한 모든 정보를 출력합니다(`--debug` 보다 더 많음). 스캔/컴파일/렌더링 흐름을 단계별로 따라가는 데 유용합니다. Helm 에는 전달되지 않습니다. |
| `--show-log-level` | | 각 로그 줄 앞에 레벨(INFO/DEBUG/…)을 붙입니다. `--debug` 또는 `--verbose` 가 활성화되면 자동으로 적용됩니다. |
| `--exception` | | 오류 시 짧은 메시지 대신 전체 예외 스택 트레이스를 출력합니다. 버그 보고나 모호한 실패 진단에 사용합니다. |
| `--experimental` | | 아래의 실험적 옵션을 해제합니다. 이것이 없으면 실험적 옵션을 전달할 때 설명과 함께 명령이 실패합니다. |
| `--unsafe` | `!!!` | Kotlin 스크립트에서 `import` 문과 정규화된 클래스 이름을 허용합니다. 스크립트가 임의 JVM 코드를 실행할 수 있으므로 완전히 신뢰하는 저장소에서만 활성화하세요. |

## 실험적 기능

실험적 기능은 안정적이지 않으며 향후 버전에서 변경될 수 있습니다. `--experimental` 플래그가 필요하며,
없으면 작업을 시작하기 전에 명령이 실패합니다.

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--yaml-merge=TYPE` | `*` | YAML 문서(예: 기본 파일과 오버레이) 병합에 사용할 알고리즘을 선택합니다. `HELM`(기본값)은 Helm 자체 병합 의미를 재현하여 결과가 Helm 과 일치합니다. `INTERNAL`은 Kube KTS 자체 알고리즘으로, 추가로 `--yaml-array-merge` 를 따릅니다. |
| `--yaml-array-merge=TYPE` | `*` | 배열 병합 방식을 제어하며 `--yaml-merge=INTERNAL` 에서만 적용됩니다. `None`은 기본 배열을 그대로 두고, `Replace`(기본값)는 오버레이 배열로 교체, `AddFirst`는 오버레이 항목을 앞에, `AddLast`는 뒤에 추가합니다. 순수 Helm 에서는 배열이 항상 교체되므로, 그 동작을 바꾸는 손잡이입니다. |

## 값 (Values)

모든 렌더링 명령(`render`, `lint`, `template`, `install`, `upgrade`, `uninstall`)은 값 파일을 받습니다. 값은 두
단계에서 사용됩니다. 렌더링 중 Kotlin 스크립트에서 사용할 수 있고 **동시에** Helm 에 전달됩니다.

| 옵션 | 마커 | 설명 |
|---|---|---|
| `-f`, `--values=FILE` | `---->` | YAML 값 파일. Kotlin 스크립트 실행에 사용되고 추가로 `-f` 로 Helm 에 전달됩니다. 반복 가능하며, 지정한 순서대로 겹쳐지고 나중 파일이 이전 파일을 덮어씁니다. 인라인 `--set*` 재정의(지원 명령)는 모든 `-f` 파일보다 우선합니다. 파일이 없으면 명령이 실패합니다. |
