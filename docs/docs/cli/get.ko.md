# get

```bash
kube-kts get <SUBCOMMAND> <RELEASE> [옵션]
```

설치된 릴리스의 확장 정보를 다운로드하는 `helm get …` 하위 명령을 묶습니다. 각 하위 명령은 기존 릴리스를 대상으로 동작하므로 **저장소도 렌더링 단계도 필요하지 않습니다** — 호출은 Helm으로 직접 전달됩니다. 하위 명령 없이 `get`을 호출하면 사용법 목록이 출력됩니다.

## 하위 명령

| 하위 명령 | Helm | 설명 |
|---|---|---|
| `get all <RELEASE>` | `helm get all` | 릴리스의 모든 정보를 다운로드합니다. |
| `get values <RELEASE>` | `helm get values` | 릴리스의 (제공 또는 계산된) 값을 다운로드합니다. |
| `get manifest <RELEASE>` | `helm get manifest` | 릴리스의 매니페스트를 다운로드합니다. |
| `get hooks <RELEASE>` | `helm get hooks` | 릴리스의 모든 훅을 다운로드합니다. |
| `get notes <RELEASE>` | `helm get notes` | 릴리스의 notes를 다운로드합니다. |
| `get metadata <RELEASE>` | `helm get metadata` | 릴리스의 메타데이터를 다운로드합니다. |

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `RELEASE` | 예 | 조회할 릴리스 이름. 위치 인자 `RELEASE`로 Helm에 전달됩니다. |

## 하위 명령별 옵션

| 하위 명령 | 옵션(모두 `---->`) |
|---|---|
| `all` | `--revision=INT`, `--template=TEMPLATE`, `-o`/`--output=FORMAT` |
| `values` | `-a`/`--all`, `--revision=INT`, `-o`/`--output=FORMAT` |
| `manifest` | `--revision=INT` |
| `hooks` | `--revision=INT` |
| `notes` | `--revision=INT` |
| `metadata` | `-o`/`--output=FORMAT` |

`--revision`은 특정 리비전을 선택하고, `values`의 `-a`/`--all`은 계산된 모든 값을 덤프하며, `--template`은 Go 템플릿을 적용하고, `-o`/`--output`은 `table`/`json`/`yaml`을 선택합니다.

## Helm 전역 옵션

모든 [Helm 전역 옵션](status.md#helm-global-options)이 Helm으로 전달됩니다.

## 전역 옵션

모든 [전역 옵션](index.md)도 사용할 수 있습니다.

## 예시

```bash
# 릴리스의 계산된 모든 값을 JSON으로 가져오기
kube-kts get values my-app --all -o json

# 특정 리비전의 렌더링된 매니페스트 가져오기
kube-kts get manifest my-app --revision 2
```
