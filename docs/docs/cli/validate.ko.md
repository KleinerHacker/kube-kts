# validate

```bash
kube-kts validate <REPOSITORY>
```

KTS 저장소를 검증합니다. 저장소를 스캔하여 모든 `*.spec.kts`, `*.lib.kts` 및 기존 YAML/템플릿 파일을
찾아내고, 저장소가 구조적으로 올바른지와 필요한 파일(예: Chart 정의)이 모두 있는지 확인합니다.
스크립트를 컴파일하거나 실행하지 **않으며** 출력 산출물도 생성하지 않습니다. 순수하게 빠른 구조 검사입니다.

CI 파이프라인에서 가장 먼저, 가장 저렴한 관문으로 사용하세요. 더 비싼 컴파일/렌더링 단계 전에 누락되거나
잘못 배치된 파일을 잡아내며, 클러스터나 Helm 에 전혀 접근하지 않습니다.

!!! tip "validate 의 위치"
    `validate` 는 *구조*만 확인합니다. Kotlin 스크립트가 컴파일·실행되는지도 확인하려면
    [`compile`](compile.md) 을, Helm YAML 도 생성하려면 [`render`](render.md) 를 사용하세요.

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `REPOSITORY` | 예 | 검증할 저장소 경로. 생략하면 현재 작업 디렉터리를 사용합니다. 존재하지 않는 경로는 즉시 실패합니다. |

## 옵션

이 명령은 [전역 옵션](index.md)만 받습니다. 여기서 특히 유용한 것은 `--verbose`(어떤 파일이 발견되는지
확인)와 `--exception`(모호한 이유로 검증이 실패할 때 스택 트레이스 확인)입니다.

## 예시

```bash
# 현재 디렉터리 검증
kube-kts validate .

# 특정 저장소를 검증하고 발견된 모든 파일 표시
kube-kts validate /path/to/repository --verbose

# CI 에서 검증하고 실패 시 전체 스택 트레이스 출력
kube-kts validate ./helm --exception
```
