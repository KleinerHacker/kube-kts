# upgrade

```bash
kube-kts upgrade <REPOSITORY> [TARGET] --name <NAME> [옵션]
```

저장소를 Helm Chart 로 렌더링한 다음 `helm upgrade` 를 실행하여 Kubernetes 클러스터의 기존 릴리스를
업데이트합니다(`--install` 과 함께면 없을 경우 설치). [`install`](install.md) 의 자연스러운 대응
명령으로, 거의 모든 플래그를 공유하며 이전 릴리스의 값을 어떻게 재사용할지 제어하는 업그레이드 전용
플래그가 몇 가지 더 있습니다.

## 동작 방식

1. 저장소를 스캔·컴파일하고 `-f`/`--set*` 값을 적용하여 (임시 또는 명시적) Chart 디렉터리로
   렌더링합니다.
2. 그 디렉터리에서 `helm upgrade <NAME> .` 를 실행하고 전달 옵션을 모두 덧붙입니다.
3. Helm 이 새 릴리스 리비전을 생성합니다. 종료 코드는 Helm 의 결과를 반영합니다. 실패 시 `--atomic` 을
   전달하지 않으면 롤백되지 않습니다.

!!! note "릴리스 이름은 옵션, 네임스페이스는 `-n`"
    릴리스 이름은 `--name` 으로 전달하며 위치 인수 `NAME` 으로 Helm 에 전달됩니다. `-n` 은 Helm 에 맞춰
    `--namespace` 용으로 예약되어 있습니다.

!!! tip "업그레이드 또는 설치와 값 처리"
    `-i`/`--install` 을 사용하면 릴리스가 없을 때 설치합니다. 기본적으로 업그레이드는 전달한 값을
    사용하며, `--reuse-values`, `--reset-values`, `--reset-then-reuse-values` 가 이전 릴리스 값의 처리
    방식을 제어합니다.

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `REPOSITORY` | 예 | 업그레이드할 저장소 경로. 생략하면 현재 작업 디렉터리를 사용합니다. |
| `TARGET` | 아니오 | 업그레이드 전에 Chart 를 렌더링할 디렉터리. 생략하면 임시 디렉터리를 사용합니다. |

## upgrade 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--name=NAME` | | 업그레이드할 릴리스 이름. 위치 인수 `NAME` 으로 Helm 에 전달됩니다. |
| `-i`, `--install` | `---->` | 동일 이름의 릴리스가 없으면 실패하지 않고 설치를 실행합니다. |
| `--atomic` | `---->` | 업그레이드 실패 시 이번 업그레이드에서 변경한 내용을 롤백합니다. `--wait` 를 포함합니다. |
| `--cleanup-on-fail` | `---->` | 업그레이드 실패 시 이번 업그레이드에서 새로 생성된 리소스의 삭제를 허용합니다. |
| `--create-namespace` | `---->` | `--install` 과 함께면 릴리스 네임스페이스가 없을 때 생성합니다. |
| `--dry-run` | `---->` | 클러스터를 바꾸지 않고 업그레이드를 시뮬레이션합니다. |
| `--enable-dns` | `---->` | 렌더링 중 템플릿이 DNS 조회를 수행하도록 허용합니다. |
| `--force` | `---->` | 교체를 통해 리소스 업데이트를 강제합니다. 파괴적일 수 있습니다. |
| `--history-max=INT` | `---->` | 릴리스별로 보관할 리비전 수를 제한합니다(`0` 은 무제한, Helm 기본 `10`). |
| `-l`, `--labels=KEY=VALUE` | `---->` | 릴리스 메타데이터에 레이블을 추가합니다. 반복 가능. |
| `--description=TEXT` | `---->` | 이 릴리스 리비전에 사용자 지정 설명을 붙입니다. |
| `-o`, `--output=FORMAT` | `---->` | 출력 형식: `table`(기본), `json`, `yaml`. |
| `--reset-values` | `---->` | 이전 릴리스를 무시하고 값을 Chart 내장 값으로 재설정합니다. |
| `--reuse-values` | `---->` | 이전 릴리스의 값을 재사용하고 새 재정의를 병합합니다. |
| `--reset-then-reuse-values` | `---->` | Chart 값으로 재설정한 다음 이전 릴리스 값을 적용하고 재정의를 병합합니다. |
| `--take-ownership` | `---->` | Helm 소유권 주석을 무시하고 기존 리소스의 소유권을 가져옵니다. 주의해서 사용하세요. |
| `--wait` | `---->` | 릴리스를 성공으로 표시하기 전에 모든 리소스가 준비될 때까지 대기합니다. `--timeout` 을 따릅니다. |
| `--wait-for-jobs` | `---->` | `--wait` 와 함께면 모든 Job 이 완료될 때까지 대기합니다. |

## 값 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--set=KEY=VALUE` | `---->` | 값을 인라인으로 재정의(예: `--set image.tag=1.2.3`). 타입은 추론됩니다. 반복 가능하며 나중 `--set` 이 이기고 `-f` 를 덮어씁니다. |
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
| `--pass-credentials` | `---->` | 리포지토리 자격 증명을 모든 도메인에 보냅니다. 신뢰하는 리포지토리만. |
| `--ca-file=FILE` | `---->` | HTTPS Chart 서버 TLS 인증서 검증에 사용할 CA 번들. |
| `--cert-file=FILE` | `---->` | Chart 서버에 대한 상호 TLS 용 클라이언트 TLS 인증서. |
| `--key-file=FILE` | `---->` | `--cert-file` 에 대응하는 클라이언트 TLS 개인 키. |
| `--insecure-skip-tls-verify` | `---->` | Chart 다운로드 시 TLS 검증을 건너뜁니다. 안전하지 않음——프로덕션에서는 피하세요. |
| `--keyring=FILE` | `---->` | 서명된 Chart 검증에 사용할 공개 키 키링(`--verify` 와 함께). 기본 `~/.gnupg/pubring.gpg`. |
| `--verify` | `---->` | 업그레이드 전에 Chart 의 출처 서명을 검증합니다. 없거나 유효하지 않으면 실패. |
| `--version=VERSION` | `---->` | Chart 버전을 선택하는 SemVer 제약. 기본은 최신 안정 버전. |
| `--devel` | `---->` | 사전 릴리스 버전도 고려합니다(`>0.0.0-0` 과 동일). |
| `--dependency-update` | `---->` | 업그레이드 전에 누락된 Chart 의존성을 다운로드/업데이트합니다. |

## 렌더링 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--no-hooks` | `---->` | 업그레이드 중 모든 Chart 수명 주기 hooks 를 건너뜁니다. |
| `--disable-openapi-validation` | `---->` | 렌더링된 매니페스트를 클러스터의 OpenAPI 스키마로 검증하지 않습니다. |
| `--name-template=TEMPLATE` | `---->` | 고정 `--name` 대신 릴리스 이름을 계산하는 Go 템플릿. |
| `--render-subchart-notes` | `---->` | 서브차트의 NOTES.txt 도 렌더링합니다. |
| `--skip-crds` | `---->` | Chart 에 포함된 CRD 를 설치하지 않습니다. |
| `--post-renderer=PATH` | `---->` | 적용 전 렌더링된 매니페스트에 대해 실행하는 실행 파일. |
| `--post-renderer-args=ARG` | `---->` | `--post-renderer` 실행 파일에 전달할 인수. 반복 가능. |
| `--timeout=DURATION` | `---->` | 단일 Kubernetes 작업을 기다리는 최대 시간. Go duration 형식(기본 `5m0s`). `--wait` 와 함께일 때 가장 관련. |

## Helm 전역 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | 릴리스가 존재하는 네임스페이스. 기본은 현재 kube-context 의 네임스페이스. |
| `--kube-context=CONTEXT` | `---->` | 사용할 kubeconfig 내 컨텍스트. 특정 클러스터/사용자를 대상으로 합니다. |
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
# 릴리스 "my-app" 업그레이드(없으면 설치)
kube-kts upgrade /path/to/repository --name my-app -i -n prod

# 이전 값을 재사용하고 이미지 태그만 재정의하는 원자적 업그레이드
kube-kts upgrade . --name my-app --atomic --reuse-values --set image.tag=1.3.0

# 클러스터를 건드리지 않고 업그레이드 미리보기
kube-kts upgrade ./helm --name my-app --dry-run
```
