# status

```bash
kube-kts status <RELEASE> [옵션]
```

`helm status` 를 실행하여 이미 설치된 릴리스의 상태를 표시합니다. 렌더링 계열 명령과 달리 `status` 는
클러스터에 이미 존재하는 릴리스를 대상으로 하므로 **저장소도 렌더링 단계도 필요하지 않으며**, 호출이
그대로 Helm 에 전달됩니다.

## 동작 방식

1. 저장소를 스캔·컴파일·렌더링하지 않습니다. KTS 스크립트는 여기서 무관합니다.
2. `helm status <RELEASE>` 를 실행하고 전달 옵션을 모두 덧붙입니다.
3. Helm 이 릴리스 상태를 출력합니다. 종료 코드는 Helm 의 결과를 반영합니다.

!!! note "릴리스 이름은 위치 인수, 네임스페이스는 `-n`"
    릴리스 이름은 첫 번째 위치 인수(`RELEASE`)로 전달하며 그대로 Helm 에 전달됩니다. `-n` 은
    `--namespace` 용으로 예약되어 있습니다.

!!! note "저장소가 필요 없음"
    `status` 는 아무것도 렌더링하지 않으므로 Kube KTS 저장소를 지정할 필요가 없습니다. 이는 렌더링이
    없는 기반 클래스 `BaseDirectHelmCommand` 로 만든 첫 번째 명령입니다.

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `RELEASE` | 예 | 조회할 릴리스 이름. 위치 인수 `RELEASE` 로 Helm 에 전달됩니다. |

## status 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--revision=INT` | `---->` | 최신이 아닌 지정한 리비전의 릴리스 상태를 표시합니다. |
| `--output=FORMAT` | `---->` | 지정한 형식으로 출력합니다: `table`(기본), `json`, `yaml`. |
| `--show-desc` | `---->` | 릴리스에 기록된 설명 메시지도 표시합니다. |
| `--show-resources` | `---->` | 릴리스에 속한 Kubernetes 리소스도 나열합니다. |

## Helm 전역 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | 릴리스가 존재하는 네임스페이스. 기본은 현재 kube-context 의 네임스페이스. 올바른 네임스페이스를 조회하도록 명시하세요. |
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
# "prod" 네임스페이스의 릴리스 상태 표시
kube-kts status my-app -n prod

# 특정 리비전을 JSON 으로 표시
kube-kts status my-app --revision 3 --output json

# 릴리스 설명과 리소스를 포함하여 표시
kube-kts status my-app --show-desc --show-resources
```
