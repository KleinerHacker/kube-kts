# search

```bash
kube-kts search <SUBCOMMAND> [KEYWORD] [옵션]
```

차트를 검색하는 `helm search …` 하위 명령을 묶습니다. 이들은 저장소 / Artifact Hub에 대해 동작하므로 **저장소도 렌더링 단계도 필요하지 않습니다** — 호출은 Helm으로 직접 전달됩니다. 하위 명령 없이 `search`를 호출하면 사용법 목록이 출력됩니다.

## 하위 명령

| 하위 명령 | Helm | 설명 |
|---|---|---|
| `search repo [KEYWORD]` | `helm search repo` | 추가한 저장소를 검색합니다. |
| `search hub [KEYWORD]` | `helm search hub` | Artifact Hub 또는 자체 hub 인스턴스를 검색합니다. |

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `KEYWORD` | 아니오 | 검색할 키워드. 위치 인자 `KEYWORD`로 Helm에 전달됩니다. |

## 하위 명령별 옵션

| 하위 명령 | 옵션(모두 `---->`) |
|---|---|
| `repo` | `--devel`, `--fail-on-no-result`, `--max-col-width=UINT`, `-o`/`--output=FORMAT`, `-r`/`--regexp`, `--version=VERSION`, `-l`/`--versions` |
| `hub` | `--endpoint=URL`, `--fail-on-no-result`, `--list-repo-url`, `--max-col-width=UINT`, `-o`/`--output=FORMAT` |

## Helm 전역 옵션

모든 [Helm 전역 옵션](status.md#helm-global-options)이 Helm으로 전달됩니다.

## 전역 옵션

모든 [전역 옵션](index.md)도 사용할 수 있습니다.

## 예시

```bash
# 추가한 저장소에서 "nginx" 검색, 모든 버전 나열
kube-kts search repo nginx -l

# Artifact Hub 검색
kube-kts search hub nginx -o json
```
