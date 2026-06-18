# push

```bash
kube-kts push <CHART> <REMOTE> [옵션]
```

`helm push`를 실행하여 패키징된 차트를 원격(OCI) 레지스트리에 업로드합니다. 기존 차트 패키지를 대상으로 동작하므로 **저장소도 렌더링 단계도 필요하지 않습니다** — 호출은 Helm으로 직접 전달됩니다.

## 동작 방식

1. 저장소를 스캔·컴파일·렌더링하지 않습니다. KTS 스크립트는 여기서 무관합니다.
2. 전달되는 모든 옵션을 덧붙여 `helm push <CHART> <REMOTE>`를 실행합니다.
3. Helm이 차트를 업로드합니다. 명령의 종료 코드는 Helm 결과를 반영합니다.

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `CHART` | 예 | 패키징된 차트 경로(`.tgz`). 위치 인자 `CHART`로 Helm에 전달됩니다. |
| `REMOTE` | 예 | 원격 레지스트리 참조. 위치 인자 `REMOTE`로 Helm에 전달됩니다. |

## push 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--ca-file=FILE` | `---->` | 레지스트리 TLS 인증서를 검증할 CA 번들. |
| `--cert-file=FILE` | `---->` | 클라이언트를 식별하는 SSL 인증서 파일. |
| `--key-file=FILE` | `---->` | 클라이언트를 식별하는 SSL 키 파일. |
| `--insecure-skip-tls-verify` | `---->` | 업로드의 TLS 인증서 검사를 건너뜁니다. |
| `--plain-http` | `---->` | 업로드에 안전하지 않은 HTTP를 사용합니다. |

## Helm 전역 옵션

모든 [Helm 전역 옵션](status.md#helm-global-options)이 Helm으로 전달됩니다.

## 전역 옵션

모든 [전역 옵션](index.md)도 사용할 수 있습니다.

## 예시

```bash
# 패키징된 차트를 OCI 레지스트리에 푸시
kube-kts push my-app-1.0.0.tgz oci://registry.example.com/charts
```
