# Kube KTS CLI

Kube KTS는 `kube-kts` 명령으로 실행할 수 있는 명령줄 도구입니다.

저장소를 처리하려면 저장소 경로를 제공하세요. 저장소 경로를 제공하지 않으면
현재 작업 디렉터리가 사용됩니다.

## 전역 옵션

- `--debug`: 로그 레벨을 포함한 디버그 정보를 출력합니다.
- `--verbose`: 로그 레벨을 포함한 모든 정보를 출력합니다.
- `--show-log-level`: 출력에 로그 레벨을 표시합니다.
- `--exception`: 오류 발생 시 전체 예외 스택 트레이스를 출력합니다.
- `--experimental`: 실험적 기능을 활성화합니다.
- `--unsafe`: 안전하지 않은 모드를 활성화하여 Kotlin 스크립트에서 `import` 문과 완전 정규화된 클래스 이름을 허용합니다.

## 실험적 기능

실험적 기능은 안정적이지 않으며 향후 버전에서 변경될 수 있습니다. 사용하려면
`--experimental` 플래그를 활성화하세요.

- `--yaml-merge`: YAML 파일 병합에 사용할 알고리즘을 지정합니다.
    - `HELM`: 표준 Helm 병합 알고리즘을 사용합니다(기본값).
    - `INTERNAL`: 사용자 정의 내부 병합 알고리즘을 사용합니다.
- `--yaml-array-merge`: `INTERNAL` 알고리즘 사용 시 배열의 병합 전략을 지정합니다.
    - `None`: 기본 배열을 변경하지 않고 유지합니다.
    - `Replace`: 기본 배열을 오버레이 배열로 교체합니다(기본값).
    - `AddFirst`: 오버레이 배열을 기본 배열의 앞에 추가합니다.
    - `AddLast`: 오버레이 배열을 기본 배열의 끝에 추가합니다.

## Validate(검증)

`kube-kts validate` 명령은 저장소를 검증합니다. 저장소가 유효하며 필요한
모든 파일을 포함하고 있는지 확인합니다.

### 매개변수

1. `REPOSITORY`: 저장소 경로. 제공하지 않으면 현재 디렉터리가 사용됩니다.

### 예제

```bash
# 현재 디렉터리 검증
kube-kts validate .

# 특정 저장소 검증
kube-kts validate /path/to/repository
```

## Compile(컴파일)

`kube-kts compile` 명령은 저장소를 컴파일합니다. 저장소에서 객체 인스턴스를
생성하며, 이는 다음 단계에서 템플릿을 렌더링하는 데 사용됩니다.

### 매개변수

1. `REPOSITORY`: 저장소 경로. 제공하지 않으면 현재 디렉터리가 사용됩니다.

### 예제

```bash
# 현재 디렉터리 컴파일
kube-kts compile .

# 특정 저장소 컴파일
kube-kts compile /path/to/repository
```

## Render(렌더링)

`kube-kts render` 명령은 저장소를 렌더링합니다. 저장소에서 Helm 파일을
생성하여 Helm과 함께 사용할 수 있도록 합니다.

### 매개변수

1. `REPOSITORY`: 저장소 경로. 제공하지 않으면 현재 디렉터리가 사용됩니다.
2. `TARGET`: Helm 파일이 렌더링될 대상 디렉터리 경로. 제공하지 않으면 임시 디렉터리가 사용됩니다.

### 예제

```bash
# 현재 디렉터리를 대상 디렉터리로 렌더링
kube-kts render . /path/to/target

# 특정 저장소를 대상 디렉터리로 렌더링
kube-kts render /path/to/repository /path/to/target
```

## Lint(린트)

`kube-kts lint` 명령은 Helm을 사용하여 저장소를 린트합니다. 렌더링된 Helm
출력이 유효한지 확인합니다.

### 매개변수

1. `REPOSITORY`: 저장소 경로. 제공하지 않으면 현재 디렉터리가 사용됩니다.
2. `TARGET`: 린트 전에 Helm 파일이 렌더링될 대상 디렉터리 경로. 제공하지 않으면 임시 디렉터리가 사용됩니다.

### 예제

```bash
# 먼저 대상 디렉터리로 렌더링한 후 현재 디렉터리를 린트
kube-kts lint . /path/to/target

# 먼저 대상 디렉터리로 렌더링한 후 특정 저장소를 린트
kube-kts lint /path/to/repository /path/to/target
```

## Template(템플릿)

`kube-kts template` 명령은 저장소의 렌더링된 템플릿을 출력합니다. 내부적으로 `helm template`을 사용합니다.

### 매개변수

1. `REPOSITORY`: 저장소 경로. 제공하지 않으면 현재 디렉터리가 사용됩니다.
2. `TARGET`: Helm 파일이 렌더링될 대상 디렉터리 경로. 제공하지 않으면 임시 디렉터리가 사용됩니다.

### 옵션

- `-n`, `--name`: **(필수)** 템플릿화할 차트의 이름.

### 예제

```bash
# 특정 이름으로 현재 디렉터리를 템플릿화
kube-kts template . /path/to/target --name my-release

# 특정 이름으로 특정 저장소를 템플릿화
kube-kts template /path/to/repository /path/to/target -n my-release
```

## Install(설치)

곧 제공될 예정입니다.

## Uninstall(제거)

곧 제공될 예정입니다.
