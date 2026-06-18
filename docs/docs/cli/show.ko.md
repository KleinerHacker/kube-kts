# show

```bash
kube-kts show <SUBCOMMAND> <CHART> [옵션]
```

차트 정보를 표시하는 `helm show …` 하위 명령(별칭 `inspect`)을 묶습니다. 이들은 원격/로컬 차트를 대상으로 동작하므로 **저장소도 렌더링 단계도 필요하지 않습니다** — 호출은 Helm으로 직접 전달됩니다. 하위 명령 없이 `show`를 호출하면 사용법 목록이 출력됩니다.

## 하위 명령

| 하위 명령 | Helm | 설명 |
|---|---|---|
| `show all <CHART>` | `helm show all` | 차트의 모든 정보를 표시합니다. |
| `show chart <CHART>` | `helm show chart` | 차트 정의(`Chart.yaml`)를 표시합니다. |
| `show values <CHART>` | `helm show values` | 차트의 기본값(`values.yaml`)을 표시합니다. |
| `show readme <CHART>` | `helm show readme` | 차트의 README를 표시합니다. |
| `show crds <CHART>` | `helm show crds` | 차트의 CRD를 표시합니다. |

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `CHART` | 예 | 검사할 차트 참조. 위치 인자 `CHART`로 Helm에 전달됩니다. |

## 옵션

각 하위 명령은 [차트 다운로드 옵션](pull.md#차트-다운로드-옵션)(`--repo`, `--version`, 자격 증명, TLS, `--verify` 등)을 받습니다. 또한 `show values`는 다음을 받습니다:

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--jsonpath=EXPRESSION` | `---->` | 출력을 필터링하는 JSONPath 표현식. |

## Helm 전역 옵션

모든 [Helm 전역 옵션](status.md#helm-global-options)이 Helm으로 전달됩니다.

## 전역 옵션

모든 [전역 옵션](index.md)도 사용할 수 있습니다.

## 예시

```bash
# 저장소 차트의 기본값 표시
kube-kts show values bitnami/nginx --version 15.0.0

# JSONPath로 값의 image만 표시
kube-kts show values bitnami/nginx --jsonpath '{.image}'
```
