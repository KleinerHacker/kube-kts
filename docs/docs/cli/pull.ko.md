# pull

```bash
kube-kts pull <CHART> [옵션]
```

`helm pull`을 실행하여 저장소에서 차트를 다운로드합니다. 원격 차트를 대상으로 동작하므로 **저장소도 렌더링 단계도 필요하지 않습니다** — 호출은 Helm으로 직접 전달됩니다.

## 동작 방식

1. 저장소를 스캔·컴파일·렌더링하지 않습니다. KTS 스크립트는 여기서 무관합니다.
2. 전달되는 모든 옵션을 덧붙여 `helm pull <CHART>`를 실행합니다.
3. Helm이 차트를 다운로드합니다. 명령의 종료 코드는 Helm 결과를 반영합니다.

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `CHART` | 예 | 다운로드할 차트 참조. 위치 인자 `CHART`로 Helm에 전달됩니다. |

## pull 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `-d`, `--destination=DIR` | `---->` | 차트를 쓸 위치. `--untardir`와 함께 지정하면 결합됩니다. |
| `--prov` | `---->` | provenance 파일을 검증 없이 가져옵니다. |
| `--untar` | `---->` | 다운로드 후 차트를 풉니다. |
| `--untardir=DIR` | `---->` | `--untar` 설정 시 차트를 푸는 디렉터리. |

## 차트 다운로드 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--repo=URL` | `---->` | 차트를 찾을 저장소 URL. |
| `--username=USER`, `--password=PASSWORD` | `---->` | 저장소 자격 증명. |
| `--pass-credentials` | `---->` | 모든 도메인에 자격 증명을 전달합니다. |
| `--ca-file=FILE`, `--cert-file=FILE`, `--key-file=FILE` | `---->` | 다운로드에 사용하는 TLS 자료. |
| `--insecure-skip-tls-verify` | `---->` | 다운로드의 TLS 인증서 검사를 건너뜁니다. |
| `--plain-http` | `---->` | 다운로드에 안전하지 않은 HTTP를 사용합니다. |
| `--keyring=FILE` | `---->` | `--verify`에 사용하는 공개 키. |
| `--verify` | `---->` | 사용 전에 패키지를 검증합니다. |
| `--version=VERSION` | `---->` | 사용할 차트 버전 제약. |
| `--devel` | `---->` | 개발 버전을 허용합니다(`>0.0.0-0`에 해당). |

## Helm 전역 옵션

모든 [Helm 전역 옵션](status.md#helm-global-options)이 Helm으로 전달됩니다.

## 전역 옵션

모든 [전역 옵션](index.md)도 사용할 수 있습니다.

## 예시

```bash
# 특정 차트 버전을 다운로드하고 압축 해제
kube-kts pull bitnami/nginx --version 15.0.0 --untar -d ./charts
```
