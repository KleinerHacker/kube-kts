# rollback

```bash
kube-kts rollback <RELEASE> [REVISION] [옵션]
```

`helm rollback`을 실행하여 릴리스를 이전 리비전으로 롤백합니다. 기존 릴리스를 대상으로 동작하므로 **저장소도 렌더링 단계도 필요하지 않습니다** — 호출은 Helm으로 직접 전달됩니다.

## 동작 방식

1. 저장소를 스캔·컴파일·렌더링하지 않습니다. KTS 스크립트는 여기서 무관합니다.
2. 전달되는 모든 옵션을 덧붙여 `helm rollback <RELEASE> [REVISION]`을 실행합니다.
3. Helm이 롤백을 수행합니다. 명령의 종료 코드는 Helm 결과를 반영합니다.

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `RELEASE` | 예 | 롤백할 릴리스 이름. 위치 인자 `RELEASE`로 Helm에 전달됩니다. |
| `REVISION` | 아니오 | 롤백할 리비전. 생략하면 Helm이 이전 리비전으로 롤백합니다. |

## rollback 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--cleanup-on-fail` | `---->` | 롤백 실패 시 이번 롤백에서 생성된 새 리소스의 삭제를 허용합니다. |
| `--dry-run` | `---->` | 아무것도 변경하지 않고 롤백을 시뮬레이션합니다. |
| `--force` | `---->` | 필요 시 삭제/재생성을 통해 리소스를 강제로 업데이트합니다. |
| `--history-max=INT` | `---->` | 릴리스당 저장되는 최대 리비전 수(`0`은 무제한). |
| `--no-hooks` | `---->` | 롤백 중 훅 실행을 방지합니다. |
| `--recreate-pods` | `---->` | 해당되는 경우 리소스의 Pod를 재시작합니다. |
| `--timeout=DURATION` | `---->` | 개별 Kubernetes 작업을 기다리는 시간. |
| `--wait` | `---->` | 릴리스를 성공으로 표시하기 전에 모든 리소스가 준비될 때까지 대기합니다. |
| `--wait-for-jobs` | `---->` | `--wait`와 함께 사용 시 모든 Job이 완료될 때까지 대기합니다. |

## Helm 전역 옵션

모든 [Helm 전역 옵션](status.md#helm-global-options)이 Helm으로 전달됩니다.

## 전역 옵션

모든 [전역 옵션](index.md)도 사용할 수 있습니다.

## 예시

```bash
# 이전 리비전으로 롤백
kube-kts rollback my-app

# 리비전 3으로 롤백하고 준비 완료 대기
kube-kts rollback my-app 3 --wait
```
