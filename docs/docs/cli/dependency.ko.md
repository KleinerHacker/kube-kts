# dependency

```bash
kube-kts dependency <SUBCOMMAND> <REPOSITORY> [TARGET] [옵션]
```

차트의 의존성을 관리하는 `helm dependency …` 하위 명령(별칭 `dep`)을 묶습니다. 각 하위 명령은 **저장소가 필요**하며 먼저 전체 *스캔 → 컴파일 → 렌더링* 파이프라인을 실행한 다음 렌더링된 차트에 대해 동작합니다(`helm dependency <sub> .`). 하위 명령 없이 `dependency`를 호출하면 사용법 목록이 출력됩니다.

## 하위 명령

| 하위 명령 | Helm | 설명 |
|---|---|---|
| `dependency build <REPOSITORY> [TARGET]` | `helm dependency build .` | `Chart.lock`을 기반으로 `charts/`를 다시 빌드합니다. |
| `dependency update <REPOSITORY> [TARGET]` | `helm dependency update .` | `Chart.yaml` 내용을 기반으로 `charts/`를 업데이트합니다. |
| `dependency list <REPOSITORY> [TARGET]` | `helm dependency list .` | 차트의 의존성을 나열합니다. |

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `REPOSITORY` | 예 | 렌더링할 Kube KTS 저장소 경로. |
| `TARGET` | 아니오 | 차트를 렌더링할 디렉터리. 생략 시 임시 디렉터리를 사용합니다. |

## 하위 명령별 옵션

| 하위 명령 | 옵션(모두 `---->`) |
|---|---|
| `build` | `--keyring=FILE`, `--skip-refresh`, `--verify` |
| `update` | `--keyring=FILE`, `--skip-refresh`, `--verify` |
| `list` | `--max-col-width=UINT` |

## 값

이 명령들은 [`-f`/`--values`](index.md#values)로 값 파일을 받아 렌더링 중 Kotlin 스크립트에 제공합니다.

## Helm 전역 옵션

모든 [Helm 전역 옵션](status.md#helm-global-options)이 Helm으로 전달됩니다.

## 전역 옵션

모든 [전역 옵션](index.md)도 사용할 수 있습니다.

## 예시

```bash
# 차트 의존성을 렌더링하고 업데이트
kube-kts dependency update ./my-repo

# 렌더링된 차트의 의존성 나열
kube-kts dependency list ./my-repo
```
