# uninstall

```bash
kube-kts uninstall <REPOSITORY> [TARGET] --name <RELEASE> [--name <RELEASE>...] [옵션]
```

저장소를 렌더링한 다음 `helm uninstall` 을 실행하여 클러스터에서 하나 이상의 릴리스를 제거합니다.
제거는 릴리스에 속한 리소스를 삭제하고 기본적으로 그 히스토리도 삭제합니다. 다른 Helm 기반 명령과
마찬가지로 kube-context 를 통해 클러스터와 통신하고 지원되는 모든 Helm uninstall 플래그를 전달합니다.

## 동작 방식

1. 저장소를 스캔·컴파일·렌더링합니다(렌더링 단계는 다른 명령과 워크플로를 통일하기 위함이며, 렌더링된
   Chart 가 작업 디렉터리로 사용됩니다).
2. `helm uninstall <RELEASE>...` 를 실행하고 전달 옵션을 모두 덧붙입니다.
3. Helm 이 각 릴리스를 제거합니다. 종료 코드는 Helm 의 결과를 반영합니다.

!!! note "릴리스 이름은 옵션, 네임스페이스는 `-n`"
    릴리스 이름은 `--name`(반복 가능)으로 전달하며 위치 인수 `RELEASE` 로 Helm 에 전달됩니다. `-n` 은
    `--namespace` 용으로 예약되어 있습니다. 최소 하나의 `--name` 이 필요합니다.

!!! warning "클러스터 리소스를 삭제합니다"
    제거는 파괴적입니다. 먼저 `--dry-run` 으로 미리 보고, 나중에 확인하거나 롤백하기 위해 히스토리를
    남기려면 `--keep-history` 를 사용하세요.

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `REPOSITORY` | 예 | 저장소 경로. 생략하면 현재 작업 디렉터리를 사용합니다. |
| `TARGET` | 아니오 | Chart 를 렌더링할 디렉터리. 생략하면 임시 디렉터리를 사용합니다. |

## uninstall 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--name=RELEASE` | | 제거할 릴리스. 반복하여 한 번의 호출로 여러 개를 제거할 수 있으며, 각각 위치 인수 `RELEASE` 로 Helm 에 전달됩니다. **최소 하나 필요.** |
| `--cascade=STRING` | `---->` | 종속 객체 삭제 방식: `background`(기본, 백그라운드 삭제), `foreground`(종속 항목을 먼저 대기), `orphan`(종속 항목을 남김). |
| `--description=TEXT` | `---->` | 제거 작업에 기록할 사람이 읽을 수 있는 사용자 지정 설명. |
| `--dry-run` | `---->` | 클러스터를 바꾸지 않고 제거를 시뮬레이션하여 제거 대상을 표시합니다. |
| `--ignore-not-found` | `---->` | 없는 릴리스를 오류가 아니라 성공으로 처리합니다. 명령을 멱등하게 만듭니다——정리 스크립트에 유용. |
| `--keep-history` | `---->` | 리소스를 삭제하고 릴리스를 삭제됨으로 표시하되, 나중에 확인하거나 롤백하기 위해 히스토리를 유지합니다. |
| `--no-hooks` | `---->` | 제거 중 pre/post-delete hooks 를 건너뜁니다. |
| `--timeout=DURATION` | `---->` | 단일 Kubernetes 작업을 기다리는 최대 시간. Go duration 형식(기본 `5m0s`). `--wait` 와 관련. |
| `--wait` | `---->` | 반환하기 전에 릴리스의 모든 리소스가 실제로 삭제될 때까지 블록합니다. |

## Helm 전역 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | 릴리스가 존재하는 네임스페이스. 기본은 현재 kube-context 의 네임스페이스. 잘못된 네임스페이스에서 제거하지 않도록 명시하세요. |
| `--kube-context=CONTEXT` | `---->` | 사용할 kubeconfig 내 컨텍스트. 현재 컨텍스트를 바꾸지 않고 특정 클러스터/사용자를 대상으로 합니다. |
| `--kubeconfig=FILE` | `---->` | 기본 대신 사용할 kubeconfig 파일 경로. |
| `--kube-apiserver=ADDRESS` | `---->` | kubeconfig 의 API 서버 주소와 포트를 재정의합니다. |
| `--kube-as-user=USER` | `---->` | 이 사용자를 가장합니다. 가장 권한이 필요합니다. |
| `--kube-as-group=GROUP` | `---->` | 이 그룹을 가장합니다. 반복 가능. |
| `--kube-ca-file=FILE` | `---->` | API 서버 TLS 인증서 검증에 사용할 CA 인증서 파일. |
| `--kube-token=TOKEN` | `---->` | kubeconfig 자격 증명 대신 인증에 사용할 베어러 토큰. |
| `--kube-tls-server-name=NAME` | `---->` | API 서버 인증서 검증에 사용할 서버 이름. |
| `--kube-insecure-skip-tls-verify` | `---->` | API 서버 인증서 검증을 건너뜁니다. 안전하지 않음——신뢰/테스트 전용. |
| `--burst-limit=INT` | `---->` | API 요청에 대한 클라이언트 측 스로틀링 버스트 한도(기본 `100`). |
| `--qps=QPS` | `---->` | API 서버와 통신 시 클라이언트 측 초당 쿼리 한도. |
| `--registry-config=FILE` | `---->` | OCI 레지스트리 설정(자격 증명) 파일 경로. |
| `--repository-cache=DIR` | `---->` | 의존성 해결에 사용하는 캐시된 리포지토리 인덱스 디렉터리. |
| `--repository-config=FILE` | `---->` | 리포지토리 이름을 URL 에 매핑하는 파일 경로(`repositories.yaml`). |

## 전역 옵션

모든 [전역 옵션](index.md)(`--debug`, `--unsafe` 등)도 사용할 수 있습니다.

## 예시

```bash
# "prod" 네임스페이스에서 단일 릴리스 제거
kube-kts uninstall /path/to/repository --name my-app -n prod

# 여러 릴리스를 히스토리를 유지하며 제거
kube-kts uninstall . --name my-app --name my-worker --keep-history

# 제거를 미리 보고 없는 릴리스를 성공으로 처리
kube-kts uninstall ./helm --name my-app --dry-run --ignore-not-found
```
