# render

```bash
kube-kts render <REPOSITORY> [TARGET] [-f FILE...]
```

저장소를 디스크의 표준 Helm Chart 로 렌더링합니다. 스캔·컴파일·실행의 전체 파이프라인을 수행하고,
결과를 일반 Helm Chart(`Chart.yaml`, `values.yaml`, 순수 YAML 로 된 `templates/` 디렉터리)로 대상
디렉터리에 기록합니다. 출력에는 Kotlin 도 Go 템플릿 로직도 없으므로 `helm` 으로 바로 사용하거나 커밋,
차이 비교, 수동 확인이 가능합니다.

`render` 는 Kube KTS 세계와 표준 도구 사이의 다리입니다. Helm 기반 명령이 내부에서 하는 모든 일은 바로
이 단계에서 시작됩니다. 명시적으로 실행하는 것이 Kube KTS 의 산출물을 *보는* 가장 좋은 방법입니다.

!!! tip "출력 확인"
    명시적 `TARGET` 을 전달하면 명령 종료 후에도 Chart 가 유지됩니다. 지정하지 않으면 Chart 는 운영체제가
    정리할 수 있는 임시 디렉터리에 기록됩니다.

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `REPOSITORY` | 예 | 렌더링할 저장소 경로. 생략하면 현재 작업 디렉터리를 사용합니다. |
| `TARGET` | 아니오 | Helm Chart 를 기록할 디렉터리. 생략하면 임시 디렉터리를 생성합니다. 기존 대상은 새로 렌더링한 Chart 로 덮어써집니다. |

## 옵션

[전역 옵션](index.md) 외에:

| 옵션 | 마커 | 설명 |
|---|---|---|
| `-f`, `--values=FILE` | `---->` | 렌더링 중 Kotlin 스크립트에서 사용할 수 있는 YAML 값 파일. 반복 가능하며 순서대로 겹쳐지고 나중 파일이 이전을 덮어씁니다. 각 파일은 존재해야 합니다. (`render` 에서는 값이 생성된 Chart 에 영향을 줍니다. `render` 는 Helm 을 호출하지 않으므로 Helm 에 전달되지 않습니다.) |

## 예시

```bash
# 현재 디렉터리를 ./out 으로 렌더링
kube-kts render . ./out

# 두 개의 값 파일을 겹쳐 특정 저장소 렌더링
kube-kts render /path/to/repository ./out -f base.yaml -f prod.yaml

# 성공 여부만 확인하기 위해 임시 디렉터리로 렌더링
kube-kts render ./helm
```
