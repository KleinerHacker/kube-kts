# verify

```bash
kube-kts verify <PATH> [옵션]
```

`helm verify`를 실행하여 패키징된 차트가 서명되었고 유효한지 확인합니다. 로컬 차트 패키지를 대상으로 동작하므로 **저장소도 렌더링 단계도 필요하지 않습니다** — 호출은 Helm으로 직접 전달됩니다.

## 동작 방식

1. 저장소를 스캔·컴파일·렌더링하지 않습니다. KTS 스크립트는 여기서 무관합니다.
2. 전달되는 모든 옵션을 덧붙여 `helm verify <PATH>`를 실행합니다.
3. Helm이 패키지를 검증합니다. 명령의 종료 코드는 Helm 결과를 반영합니다.

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `PATH` | 예 | 검증할 패키징된 차트 경로. 위치 인자 `PATH`로 Helm에 전달됩니다. |

## verify 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--keyring=FILE` | `---->` | 검증에 사용하는 공개 키가 포함된 키링. |

## Helm 전역 옵션

모든 [Helm 전역 옵션](status.md#helm-global-options)이 Helm으로 전달됩니다.

## 전역 옵션

모든 [전역 옵션](index.md)도 사용할 수 있습니다.

## 예시

```bash
# 키링으로 패키징된 차트 검증
kube-kts verify my-app-1.0.0.tgz --keyring ~/.gnupg/pubring.gpg
```
