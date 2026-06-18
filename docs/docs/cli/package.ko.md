# package

```bash
kube-kts package <REPOSITORY> [TARGET] [옵션]
```

KTS 저장소를 일반 Helm 차트로 렌더링한 다음 이에 대해 `helm package .`를 실행하여 버전이 지정된 차트 아카이브(`.tgz`)를 생성합니다. 릴리스 명령과 달리 `package`는 **저장소가 필요**하며 먼저 전체 *스캔 → 컴파일 → 렌더링* 파이프라인을 실행합니다.

## 동작 방식

1. 저장소를 스캔하고 Kotlin 스크립트를 컴파일·평가한 뒤 결과를 `TARGET` 디렉터리(생략 시 임시 디렉터리)로 렌더링합니다.
2. 해당 디렉터리에서 전달되는 모든 옵션을 덧붙여 `helm package .`를 실행합니다.
3. Helm이 차트 아카이브를 작성합니다. 명령의 종료 코드는 Helm 결과를 반영합니다.

## 매개변수

| 매개변수 | 필수 | 설명 |
|---|---|---|
| `REPOSITORY` | 예 | 렌더링하고 패키징할 Kube KTS 저장소 경로. |
| `TARGET` | 아니오 | 차트를 렌더링할 디렉터리. 생략 시 임시 디렉터리를 사용합니다. |

## package 옵션

| 옵션 | 마커 | 설명 |
|---|---|---|
| `--app-version=VERSION` | `---->` | 차트의 `appVersion`을 설정합니다. |
| `--version=VERSION` | `---->` | 차트의 (semver) `version`을 설정합니다. |
| `-d`, `--destination=DIR` | `---->` | 차트 아카이브를 쓸 위치. |
| `-u`, `--dependency-update` | `---->` | 패키징 전에 의존성을 `Chart.yaml`에서 `charts/`로 업데이트합니다. |
| `--sign` | `---->` | PGP 개인 키로 패키지에 서명합니다. |
| `--key=NAME` | `---->` | 서명 키 이름(`--sign`과 함께 사용). |
| `--keyring=FILE` | `---->` | 공개 키링 위치. |
| `--pass-stdin` | `---->` | PGP 암호를 stdin에서 읽습니다(`--sign`과 함께 사용). |

## 값

`package`는 [`-f`/`--values`](index.md#values)로 값 파일을 받아 렌더링 중 Kotlin 스크립트에 제공합니다.

## Helm 전역 옵션

모든 [Helm 전역 옵션](status.md#helm-global-options)이 Helm으로 전달됩니다.

## 전역 옵션

모든 [전역 옵션](index.md)도 사용할 수 있습니다.

## 예시

```bash
# 명시적 버전으로 저장소를 렌더링하고 패키징
kube-kts package ./my-repo --version 1.2.3 --app-version 2.0.0 -d ./dist
```
