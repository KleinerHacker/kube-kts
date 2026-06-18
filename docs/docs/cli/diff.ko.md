# diff

```bash
kube-kts diff upgrade <REPOSITORY> [TARGET] --name <RELEASE> [옵션]
```

외부 **`helm-diff` 플러그인** 위에 구축된 diff 하위 명령을 묶습니다. KTS 저장소를 렌더링하고 그 결과를 클러스터와 비교하여 작업이 적용할 변경 사항을 미리 봅니다. 각 하위 명령은 **저장소가 필요**하며 먼저 전체 *스캔 → 컴파일 → 렌더링* 파이프라인을 실행합니다. 하위 명령 없이 `diff`를 호출하면 사용법 목록이 출력됩니다.

!!! note "helm-diff 플러그인 필요"
    이 명령들은 외부 플러그인이 제공하는 `helm diff …`를 호출합니다.
    `helm plugin install https://github.com/databus23/helm-diff`로 설치하세요.

## 하위 명령

| 하위 명령 | Helm | 설명 |
|---|---|---|
| `diff upgrade <REPOSITORY> [TARGET]` | `helm diff upgrade <RELEASE> .` | `upgrade`가 적용할 변경 사항을 미리 봅니다. |

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `REPOSITORY` | 예 | 렌더링할 Kube KTS 저장소 경로. |
| `TARGET` | 아니오 | 차트를 렌더링할 디렉터리. 생략 시 임시 디렉터리를 사용합니다. |

!!! note "릴리스 이름은 `--name`으로 전달"
    `REPOSITORY`/`TARGET`이 이미 위치 인자를 차지하므로 릴리스 이름은 `--name`으로 전달되며 위치 인자 `RELEASE`로 플러그인에 전달됩니다. `-n`은 `--namespace`용으로 예약되어 있습니다.

## diff 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--name=NAME` | | 릴리스 이름(위치 인자 `RELEASE`로 플러그인에 전달). |
| `--detailed-exitcode` | `---->` | 변경 사항이 있으면 종료 코드 `2`를 반환합니다. |
| `--context=NUM` | `---->` | 변경 주위에 `NUM`줄의 컨텍스트를 출력합니다(`-1`은 전체 컨텍스트). |
| `--show-secrets` | `---->` | 출력에서 secret 값을 가리지 않습니다. |
| `--no-hooks` | `---->` | 훅 diff를 비활성화합니다. |
| `--include-tests` | `---->` | Helm test 훅의 diff를 활성화합니다. |
| `--reset-values` | `---->` | 값을 차트 내장 값으로 재설정하고 새 값을 병합합니다. |
| `--reuse-values` | `---->` | 이전 릴리스의 값을 재사용하고 새 값을 병합합니다. |
| `--normalize-manifests` | `---->` | diff 전에 매니페스트를 정규화하여 스타일 차이를 제외합니다. |

## 값

`diff upgrade`는 [`--set` 계열](install.md)과 [`-f`/`--values`](index.md#values) 값 파일을 받으며, 이는 Kotlin 스크립트와 플러그인 모두에 제공됩니다.

## Helm 전역 옵션

모든 [Helm 전역 옵션](status.md#helm-global-options)이 Helm으로 전달됩니다.

## 전역 옵션

모든 [전역 옵션](index.md)도 사용할 수 있습니다.

## 예시

```bash
# upgrade가 적용할 변경 사항 미리 보기
kube-kts diff upgrade ./my-repo --name my-app

# 변경 사항이 있으면 빌드 실패(종료 코드 2)
kube-kts diff upgrade ./my-repo --name my-app --detailed-exitcode
```
