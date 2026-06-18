# env

```bash
kube-kts env [NAME] [옵션]
```

`helm env`를 실행하여 Helm의 환경 정보를 출력합니다. 순수히 정보 제공용이며 **저장소도 렌더링 단계도 필요하지 않습니다** — 호출은 Helm으로 직접 전달됩니다.

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `NAME` | 아니오 | 출력할 단일 환경 변수 이름. 생략하면 모든 변수를 출력합니다. 위치 인자 `NAME`으로 Helm에 전달됩니다. |

## Helm 전역 옵션

모든 [Helm 전역 옵션](status.md#helm-global-options)이 Helm으로 전달됩니다.

## 전역 옵션

모든 [전역 옵션](index.md)도 사용할 수 있습니다.

## 예시

```bash
# 모든 Helm 환경 변수 출력
kube-kts env

# 단일 변수 출력
kube-kts env HELM_BIN
```
