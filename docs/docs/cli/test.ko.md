# test

```bash
kube-kts test <RELEASE> [옵션]
```

`helm test`를 실행하여 릴리스에 정의된 테스트를 실행합니다. 기존 릴리스를 대상으로 동작하므로 **저장소도 렌더링 단계도 필요하지 않습니다** — 호출은 Helm으로 직접 전달됩니다.

## 동작 방식

1. 저장소를 스캔·컴파일·렌더링하지 않습니다. KTS 스크립트는 여기서 무관합니다.
2. 전달되는 모든 옵션을 덧붙여 `helm test <RELEASE>`를 실행합니다.
3. Helm이 테스트 훅을 실행합니다. 명령의 종료 코드는 Helm 결과를 반영합니다.

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `RELEASE` | 예 | 테스트할 릴리스 이름. 위치 인자 `RELEASE`로 Helm에 전달됩니다. |

## test 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--filter=KEY=VALUE` | `---->` | 속성(예: `name=...`)으로 테스트를 선택하거나 `!attribute=value`로 제외합니다. 반복 가능. |
| `--hide-notes` | `---->` | 테스트 출력에 notes를 표시하지 않습니다. |
| `--logs` | `---->` | 모든 테스트 완료 후 테스트 Pod의 로그를 덤프합니다. |
| `--timeout=DURATION` | `---->` | 개별 Kubernetes 작업을 기다리는 시간. |

## Helm 전역 옵션

모든 [Helm 전역 옵션](status.md#helm-global-options)이 Helm으로 전달됩니다.

## 전역 옵션

모든 [전역 옵션](index.md)도 사용할 수 있습니다.

## 예시

```bash
# 릴리스의 테스트를 실행하고 로그 표시
kube-kts test my-app --logs

# 특정 테스트만 실행
kube-kts test my-app --filter name=my-test
```
