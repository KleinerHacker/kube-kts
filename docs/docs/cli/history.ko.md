# history

```bash
kube-kts history <RELEASE> [옵션]
```

`helm history`를 실행하여 릴리스의 리비전 기록을 표시합니다. 기존 릴리스를 대상으로 동작하므로 **저장소도 렌더링 단계도 필요하지 않습니다** — 호출은 Helm으로 직접 전달됩니다.

## 동작 방식

1. 저장소를 스캔·컴파일·렌더링하지 않습니다. KTS 스크립트는 여기서 무관합니다.
2. 전달되는 모든 옵션을 덧붙여 `helm history <RELEASE>`를 실행합니다.
3. Helm이 리비전 기록을 출력합니다. 명령의 종료 코드는 Helm 결과를 반영합니다.

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `RELEASE` | 예 | 조회할 릴리스 이름. 위치 인자 `RELEASE`로 Helm에 전달됩니다. |

## history 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--max=INT` | `---->` | 기록에 포함할 리비전의 최대 개수. |
| `-o`, `--output=FORMAT` | `---->` | 출력 형식: `table`(기본값), `json`, `yaml`. |

## Helm 전역 옵션

모든 [Helm 전역 옵션](status.md#helm-global-options)이 Helm으로 전달됩니다.

## 전역 옵션

모든 [전역 옵션](index.md)도 사용할 수 있습니다.

## 예시

```bash
# 릴리스의 전체 리비전 기록 표시
kube-kts history my-app

# 최근 5개 리비전을 JSON으로 표시
kube-kts history my-app --max 5 -o json
```
