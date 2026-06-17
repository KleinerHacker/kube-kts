# template

```bash
kube-kts template <REPOSITORY> [TARGET] --name <NAME> [옵션]
```

저장소를 Helm Chart 로 렌더링한 다음 `helm template` 을 실행하여 완전히 렌더링된 Kubernetes 매니페스트를
표준 출력으로 인쇄합니다. [`install`](install.md) 과 달리 (`--validate` 로 명시적으로 선택하지 않는 한)
클러스터에 연결하거나 변경하지 **않습니다**. 따라서 적용될 내용을 확인하는 가장 안전한 방법입니다. 변경
차이 비교, 출력을 `kubectl apply`/GitOps 에 전달, 값이 결과에 미치는 영향 디버깅에 이상적입니다.

## 동작 방식

1. 저장소를 스캔·컴파일하고 `-f`/`--set*` 값을 적용하여 (임시 또는 명시적) Chart 디렉터리로
   렌더링합니다.
2. 그 디렉터리에서 `helm template <NAME> .` 를 실행하고 전달 옵션을 모두 덧붙입니다.
3. 렌더링된 매니페스트를 표준 출력(또는 `--output-dir` 로 파일)에 씁니다.

!!! note "릴리스 이름은 옵션, 네임스페이스는 `-n`"
    릴리스 이름은 `--name` 으로 전달하며 위치 인수 `NAME` 으로 Helm 에 전달됩니다. `-n` 은 Helm 에 맞춰
    `--namespace` 용으로 예약되어 있습니다. 이름이 중요하지 않으면 `--generate-name` 을 사용하세요.

!!! tip "로컬 vs 클러스터 인식 렌더링"
    기본적으로 template 은 완전히 오프라인입니다. `--validate` 를 추가하면 실제 클러스터에 대해 매니페스트를
    검증하고, `-a`/`--kube-version` 으로 렌더링 중 사용하는 API 기능을 고정할 수 있습니다.

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `REPOSITORY` | 예 | template 할 저장소 경로. 생략하면 현재 작업 디렉터리를 사용합니다. |
| `TARGET` | 아니오 | template 전에 Chart 를 렌더링할 디렉터리. 생략하면 임시 디렉터리를 사용합니다. |

## template 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--name=NAME` | | 렌더링 시 사용할 릴리스 이름. 위치 인수 `NAME` 으로 Helm 에 전달됩니다. `--generate-name` 과 함께일 때만 생략. |
| `-a`, `--api-versions=VERSIONS` | `---->` | `Capabilities.APIVersions` 로 사용 가능하다고 보고할 API 버전(예: `networking.k8s.io/v1`). 클러스터에 질의하지 않고 특정 API 가 있는 것처럼 렌더링할 수 있습니다. 반복 가능. |
| `--include-crds` | `---->` | Chart 의 CRD 를 출력에 포함합니다. `helm template` 은 기본적으로 생략합니다. |
| `--is-upgrade` | `---->` | `.Release.IsUpgrade` 를 설정(`.Release.IsInstall` 해제)하여 업그레이드로 렌더링해 업그레이드 전용 분기를 실행합니다. |
| `--kube-version=VERSION` | `---->` | `Capabilities.KubeVersion` 으로 보고할 Kubernetes 버전(예: `1.29`). 특정 클러스터 버전용으로 오프라인 렌더링에 유용. |
| `--output-dir=DIR` | `---->` | 모든 것을 표준 출력으로 내보내는 대신 렌더링된 각 템플릿을 이 디렉터리의 개별 파일에 씁니다. |
| `-s`, `--show-only=TEMPLATE` | `---->` | 지정한 템플릿 경로가 생성하는 매니페스트로만 출력을 제한합니다(예: `templates/deployment.yaml`). 반복 가능. |
| `--skip-tests` | `---->` | 테스트 매니페스트(Helm 테스트로 주석된 리소스)를 출력에서 생략합니다. |
| `--validate` | `---->` | 현재 가리키는 클러스터에 대해 렌더링된 매니페스트를 검증합니다. 클러스터 액세스가 필요하며 template 이 클러스터 인식 작업이 됩니다. |
| `--dry-run` | `---->` | 렌더링 중 설치를 시뮬레이션하여 dry-run 상태를 확인하는 분기에 영향을 줍니다. |
| `-g`, `--generate-name` | `---->` | Helm 이 릴리스 이름을 생성하게 합니다. `--name` 대신 사용. |
| `-l`, `--labels=KEY=VALUE` | `---->` | (시뮬레이션된) 릴리스 메타데이터에 레이블을 추가합니다. 반복 가능. |
| `--create-namespace` | `---->` | 렌더링 출력에서 네임스페이스를 생성 대상으로 표시합니다. 설치 시 플래그와 동일. |

## 값 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--set=KEY=VALUE` | `---->` | 값을 인라인으로 재정의(예: `--set replicas=3`). 타입은 추론됩니다. 반복 가능하며 나중 `--set` 이 이기고 `-f` 를 덮어씁니다. |
| `--set-string=KEY=VALUE` | `---->` | `--set` 과 비슷하지만 값을 항상 문자열로 유지합니다. 반복 가능. |
| `--set-file=KEY=PATH` | `---->` | `PATH` 파일의 전체 내용을 `KEY` 의 값으로 사용합니다. 반복 가능. |
| `--set-json=KEY=JSON` | `---->` | JSON 식에서 값을 설정하여 객체/배열을 표현합니다. 반복 가능. |
| `--set-literal=KEY=VALUE` | `---->` | 작성한 그대로 값을 설정하며 타입 변환을 하지 않습니다. 반복 가능. |
| `-f`, `--values=FILE` | `---->` | 렌더링에 사용되고 Helm 에 전달되는 YAML 값 파일. 반복 가능하며 순서대로 겹쳐지고 `--set*` 이 이를 덮어씁니다. |

## Chart 소스 및 검증 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--repo=URL` | `---->` | 로컬 경로 대신 이 리포지토리 URL 에서 Chart 를 가져옵니다. |
| `--username=USER` | `---->` | Chart 리포지토리 사용자 이름(`--password` 와 함께). |
| `--password=PASSWORD` | `---->` | Chart 리포지토리 비밀번호. 명령줄보다 시크릿/환경 변수를 권장. |
| `--pass-credentials` | `---->` | 리포지토리 자격 증명을 Chart 호스트뿐 아니라 모든 도메인에 보냅니다. 신뢰하는 리포지토리만. |
| `--ca-file=FILE` | `---->` | HTTPS Chart 서버 TLS 인증서 검증에 사용할 CA 번들. |
| `--cert-file=FILE` | `---->` | Chart 서버에 대한 상호 TLS 용 클라이언트 TLS 인증서. |
| `--key-file=FILE` | `---->` | `--cert-file` 에 대응하는 클라이언트 TLS 개인 키. |
| `--insecure-skip-tls-verify` | `---->` | Chart 다운로드 시 TLS 검증을 건너뜁니다. 안전하지 않음——프로덕션에서는 피하세요. |
| `--keyring=FILE` | `---->` | 서명된 Chart 검증에 사용할 공개 키 키링(`--verify` 와 함께). 기본 `~/.gnupg/pubring.gpg`. |
| `--verify` | `---->` | 사용 전에 Chart 의 출처 서명을 검증합니다. 없거나 유효하지 않으면 실패. |
| `--version=VERSION` | `---->` | Chart 버전을 선택하는 SemVer 제약. 기본은 최신 안정 버전. |
| `--devel` | `---->` | 사전 릴리스 버전도 고려합니다(`>0.0.0-0` 과 동일). |
| `--dependency-update` | `---->` | template 전에 누락된 Chart 의존성을 다운로드/업데이트합니다. |

## 렌더링 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--no-hooks` | `---->` | 렌더링 중 모든 Chart 수명 주기 hooks 를 건너뜁니다. |
| `--disable-openapi-validation` | `---->` | 렌더링된 매니페스트를 OpenAPI 스키마로 검증하지 않습니다. |
| `--name-template=TEMPLATE` | `---->` | 고정 `--name` 대신 릴리스 이름을 계산하는 Go 템플릿. |
| `--render-subchart-notes` | `---->` | 서브차트의 NOTES.txt 도 렌더링합니다. |
| `--skip-crds` | `---->` | Chart 에 포함된 CRD 를 렌더링하지 않습니다(`--include-crds` 의 반대). |
| `--post-renderer=PATH` | `---->` | 렌더링된 매니페스트에 대해 실행하는 실행 파일. Kustomize 식 후처리가 가능. |
| `--post-renderer-args=ARG` | `---->` | `--post-renderer` 실행 파일에 전달할 인수. 반복 가능. |
| `--timeout=DURATION` | `---->` | 단일 Kubernetes 작업을 기다리는 최대 시간. Go duration 형식(기본 `5m0s`). 주로 `--validate` 와 관련. |

## Helm 전역 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | 렌더링 시 가정하는 네임스페이스(네임스페이스 리소스와 `.Release.Namespace` 에 영향). 기본은 현재 kube-context 의 네임스페이스. |
| `--kube-context=CONTEXT` | `---->` | 사용할 kubeconfig 내 컨텍스트(`--validate` 와 관련). |
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
# 릴리스 "my-app" 의 렌더링된 매니페스트 인쇄
kube-kts template /path/to/repository --name my-app

# 단일 템플릿만 CRD 포함, 인라인 재정의와 함께
kube-kts template . --name my-app -s templates/deployment.yaml --include-crds --set replicas=3

# 특정 클러스터 버전용으로 렌더링하여 디렉터리에 출력
kube-kts template ./helm --name my-app --kube-version 1.29 --output-dir ./manifests
```
