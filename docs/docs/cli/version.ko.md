# version

```bash
kube-kts version [옵션]
```

`helm version`을 실행하여 Helm 버전 정보를 출력합니다. 순수히 정보 제공용이며 **저장소도 렌더링 단계도 필요하지 않습니다** — 호출은 Helm으로 직접 전달됩니다.

## version 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--short` | `---->` | 버전 번호만 출력합니다. |
| `--template=TEMPLATE` | `---->` | 버전 문자열 형식용 Go 템플릿. |

## Helm 전역 옵션

모든 [Helm 전역 옵션](status.md#helm-global-options)이 Helm으로 전달됩니다.

## 전역 옵션

모든 [전역 옵션](index.md)도 사용할 수 있습니다.

## 예시

```bash
# Helm 전체 버전 출력
kube-kts version

# 버전 번호만 출력
kube-kts version --short
```
