# repo

```bash
kube-kts repo <SUBCOMMAND> [ARGS] [옵션]
```

차트 저장소를 관리하는 `helm repo …` 하위 명령을 묶습니다. 이들은 로컬 Helm 구성에 대해 동작하므로 **저장소도 렌더링 단계도 필요하지 않습니다** — 호출은 Helm으로 직접 전달됩니다. 하위 명령 없이 `repo`를 호출하면 사용법 목록이 출력됩니다.

## 하위 명령

| 하위 명령 | Helm | 설명 |
|---|---|---|
| `repo add <NAME> <URL>` | `helm repo add` | 차트 저장소를 추가합니다. |
| `repo update [REPO...]` | `helm repo update` | 지정한(또는 모든) 저장소의 로컬 캐시를 업데이트합니다. |
| `repo list` | `helm repo list` | 구성된 차트 저장소를 나열합니다. |
| `repo remove <REPO...>` | `helm repo remove` | 하나 이상의 차트 저장소를 제거합니다. |

## 하위 명령별 옵션

| 하위 명령 | 옵션(모두 `---->`) |
|---|---|
| `add` | `--username=USER`, `--password=PASSWORD`, `--pass-credentials`, `--ca-file=FILE`, `--cert-file=FILE`, `--key-file=FILE`, `--insecure-skip-tls-verify`, `--no-update`, `--force-update`, `--allow-deprecated-repos` |
| `update` | `--fail-on-repo-update-fail` |
| `list` | `-o`/`--output=FORMAT` |
| `remove` | – |

## Helm 전역 옵션

모든 [Helm 전역 옵션](status.md#helm-global-options)이 Helm으로 전달됩니다.

## 전역 옵션

모든 [전역 옵션](index.md)도 사용할 수 있습니다.

## 예시

```bash
# Bitnami 저장소 추가
kube-kts repo add bitnami https://charts.bitnami.com/bitnami

# 모든 저장소 업데이트
kube-kts repo update

# 저장소를 JSON으로 나열
kube-kts repo list -o json

# 저장소 제거
kube-kts repo remove bitnami
```
