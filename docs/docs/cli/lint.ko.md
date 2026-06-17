# lint

```bash
kube-kts lint <REPOSITORY> [TARGET] [옵션]
```

저장소를 Helm Chart 로 렌더링한 다음 그에 대해 `helm lint` 를 실행합니다. lint 는 렌더링된 Chart 의
문제와 모범 사례 위반——필수 필드 누락, 잘못된 `Chart.yaml`, 유효한 YAML 을 생성하지 않는 템플릿, 권장
되지만 없는 메타데이터 등——을 검사하여 정보, 경고 또는 오류로 보고합니다.

Kube KTS 가 먼저 렌더링하므로 `lint` 는 값이 이미 적용된, Helm 이 실제로 받게 될 *진짜* Chart 를
검증합니다. [`compile`](compile.md) 다음의 자연스러운 단계입니다. compile 은 스크립트가 동작함을, lint
는 결과 Chart 가 건전함을 증명합니다.

## 동작 방식

1. 저장소를 스캔·컴파일하여 (임시 또는 명시적) Chart 디렉터리로 렌더링합니다.
2. 그 디렉터리에서 `helm lint .` 를 실행하고 전달되는 옵션을 모두 덧붙입니다.
3. Kube KTS 와 Helm 의 출력을 함께 표시합니다. 명령의 종료 코드는 Helm 의 결과를 반영합니다.

!!! tip "경고에서 빌드 실패시키기"
    기본적으로 `helm lint` 는 오류에서만 실패합니다. `--strict` 를 추가하면 경고에서도 실패하며, 보통
    CI 에서 원하는 동작입니다.

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `REPOSITORY` | 예 | lint 할 저장소 경로. 생략하면 현재 작업 디렉터리를 사용합니다. |
| `TARGET` | 아니오 | lint 전에 Chart 를 렌더링할 디렉터리. 생략하면 임시 디렉터리를 사용합니다. |

## lint 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--quiet` | `---->` | 정보 메시지를 억제하고 경고와 오류만 출력합니다. 간결한 CI 로그에 유용합니다. |
| `--strict` | `---->` | 경고를 실패로 처리하여 어떤 lint 경고든 0 이 아닌 종료 코드를 만듭니다. 스타일/모범 사례 문제를 통과시키지 않으려는 파이프라인에 권장합니다. |
| `--with-subcharts` | `---->` | 최상위 Chart 외에 의존하는 서브차트도 lint 합니다. |

## 값 옵션

Chart 값을 설정/재정의하며 Helm 에 전달됩니다. [`-f`/`--values`](index.md) 파일을 보완합니다.

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--set=KEY=VALUE` | `---->` | 값을 인라인으로 재정의합니다(예: `--set image.tag=1.2.3`). 타입은 추론됩니다. 반복 가능하며 나중 `--set` 이 이기고 `-f` 파일을 덮어씁니다. |
| `--set-string=KEY=VALUE` | `---->` | `--set` 과 비슷하지만 값을 항상 문자열로 유지합니다(예: `"01"` 보존). 반복 가능. |
| `--set-file=KEY=PATH` | `---->` | `PATH` 파일의 전체 내용을 `KEY` 의 값으로 사용합니다(인증서나 스크립트에 유용). 반복 가능. |
| `--set-json=KEY=JSON` | `---->` | JSON 식에서 값을 설정하여 객체와 배열을 표현합니다(예: `--set-json 'ports=[80,443]'`). 반복 가능. |
| `--set-literal=KEY=VALUE` | `---->` | 작성한 그대로 값을 설정하며 타입 변환을 전혀 하지 않습니다. 반복 가능. |
| `-f`, `--values=FILE` | `---->` | 렌더링에 사용되고 Helm 에 전달되는 YAML 값 파일. 반복 가능하며 순서대로 겹쳐집니다. |

## Helm 전역 옵션

클러스터 연결과 Helm 환경을 구성하며 그대로 Helm 에 전달됩니다. (`lint` 는 오프라인이므로 클러스터 관련
옵션은 대개 무관하지만 일관성을 위해 허용됩니다.)

| 옵션 | 마커 | 설명 |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | 요청에서 가정하는 네임스페이스 범위. 기본값은 현재 kube-context 의 네임스페이스입니다. |
| `--kube-context=CONTEXT` | `---->` | 사용할 kubeconfig 내 컨텍스트. 현재 컨텍스트를 바꾸지 않고 특정 클러스터/사용자를 대상으로 할 수 있습니다. |
| `--kubeconfig=FILE` | `---->` | `$KUBECONFIG` / `~/.kube/config` 대신 사용할 kubeconfig 파일 경로. |
| `--kube-apiserver=ADDRESS` | `---->` | kubeconfig 의 API 서버 주소와 포트를 재정의합니다. |
| `--kube-as-user=USER` | `---->` | 이 사용자를 가장합니다(RBAC 사용자 가장). 가장 권한이 필요합니다. |
| `--kube-as-group=GROUP` | `---->` | 이 그룹을 가장합니다. 여러 그룹을 위해 반복 가능. |
| `--kube-ca-file=FILE` | `---->` | API 서버 TLS 인증서 검증에 사용할 CA 인증서 파일. |
| `--kube-token=TOKEN` | `---->` | kubeconfig 자격 증명 대신 인증에 사용할 베어러 토큰. |
| `--kube-tls-server-name=NAME` | `---->` | API 서버 인증서 검증에 사용할 서버 이름(URL 호스트와 다를 때). |
| `--kube-insecure-skip-tls-verify` | `---->` | API 서버 인증서 검증을 건너뜁니다. 안전하지 않음——신뢰/테스트 환경 전용. |
| `--burst-limit=INT` | `---->` | API 요청에 대한 클라이언트 측 스로틀링 버스트 한도(기본 `100`). |
| `--qps=QPS` | `---->` | API 서버와 통신 시 클라이언트 측 초당 쿼리 한도. 소수 허용. |
| `--registry-config=FILE` | `---->` | OCI 레지스트리 설정(자격 증명) 파일 경로. |
| `--repository-cache=DIR` | `---->` | 의존성 해결에 사용하는 캐시된 리포지토리 인덱스 디렉터리. |
| `--repository-config=FILE` | `---->` | 리포지토리 이름을 URL 에 매핑하는 파일 경로(`repositories.yaml`). |

## 전역 옵션

모든 [전역 옵션](index.md)(`--debug`, `--unsafe` 등)도 사용할 수 있습니다.

## 예시

```bash
# 현재 디렉터리 lint
kube-kts lint .

# 추가 값 파일과 인라인 재정의를 사용한 엄격 lint
kube-kts lint /path/to/repository ./out --strict -f prod.yaml --set image.tag=1.2.3

# 서브차트를 포함하여 조용히 lint
kube-kts lint ./helm --with-subcharts --quiet
```
