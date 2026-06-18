# registry

```bash
kube-kts registry <SUBCOMMAND> <HOST> [옵션]
```

OCI 레지스트리 인증을 관리하는 `helm registry …` 하위 명령을 묶습니다. 이들은 로컬 레지스트리 구성에 대해 동작하므로 **저장소도 렌더링 단계도 필요하지 않습니다** — 호출은 Helm으로 직접 전달됩니다. 하위 명령 없이 `registry`를 호출하면 사용법 목록이 출력됩니다.

## 하위 명령

| 하위 명령 | Helm | 설명 |
|---|---|---|
| `registry login <HOST>` | `helm registry login` | OCI 레지스트리에 로그인합니다. |
| `registry logout <HOST>` | `helm registry logout` | OCI 레지스트리에서 로그아웃합니다. |

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `HOST` | 예 | 레지스트리 호스트. 위치 인자 `HOST`로 Helm에 전달됩니다. |

## 하위 명령별 옵션

| 하위 명령 | 옵션(모두 `---->`) |
|---|---|
| `login` | `-u`/`--username=USER`, `-p`/`--password=PASSWORD`, `--password-stdin`, `--insecure`, `--ca-file=FILE`, `--cert-file=FILE`, `--key-file=FILE`, `--plain-http` |
| `logout` | – |

## Helm 전역 옵션

모든 [Helm 전역 옵션](status.md#helm-global-options)이 Helm으로 전달됩니다.

## 전역 옵션

모든 [전역 옵션](index.md)도 사용할 수 있습니다.

## 예시

```bash
# 레지스트리에 로그인(비밀번호를 stdin에서 읽기)
echo "$TOKEN" | kube-kts registry login registry.example.com -u robot --password-stdin

# 로그아웃
kube-kts registry logout registry.example.com
```
