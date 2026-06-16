# 컨테이너

컨테이너는 Pod Spec의 `containers` 블록에서 정의합니다. 각 메인 컨테이너에는 이름과 이미지가 필요합니다.

```kotlin
containers {
    container("app", "registry.example.com/demo:1.0.0") {
        imagePullPolicy = ContainerSpec.ImagePullPolicy.IfNotPresent
        workingDir = "/app"

        command("java")
        args("-jar", "app.jar")
    }
}
```

## 핵심 속성

| 속성 / 메서드 | 설명 |
| :--- | :--- |
| `imagePullPolicy` | 이미지 풀(pull) 동작: `Always`, `IfNotPresent`, `Never`. |
| `ports { port(containerPort) { ... } }` | 선택적 이름과 프로토콜을 가진 컨테이너 포트. |
| `env(name) { ... }` | 단일 환경 변수. |
| `envFrom { ... }` | ConfigMap 또는 Secret에서 가져오는 환경 변수. |
| `resources { requests { ... } limits { ... } }` | CPU, 메모리, 스토리지의 요청(requests)과 제한(limits). |
| `volumeMounts { volumeMount(name, mountPath) { ... } }` | Pod 볼륨의 마운트. |
| `livenessProbe { ... }` | 컨테이너를 재시작해야 하는지 확인합니다. |
| `readinessProbe { ... }` | 컨테이너가 트래픽을 받을 수 있는지 확인합니다. |
| `startupProbe { ... }` | 긴 시작 단계를 위한 확인. |
| `lifecycle { ... }` | `postStart` 및 `preStop` 같은 수명 주기 훅. |
| `securityContext { ... }` | 컨테이너 수준의 보안 옵션. |
| `command(...)` | 이미지의 entrypoint를 재정의합니다. |
| `args(...)` | 이미지의 인수를 재정의하거나 추가합니다. |
| `workingDir` | 컨테이너 내부의 작업 디렉터리. |

## 포트

```kotlin
container("app", "nginx:1.27") {
    ports {
        port(8080) {
            name = "http"
            protocol = Protocol.TCP
        }
        port(8443) {
            name = "https"
            protocol = Protocol.TCP
        }
    }
}
```

명명된 포트는 이후 Service나 프로브 등에서 참조할 수 있습니다.

## 환경 변수

```kotlin
container("app", "registry.example.com/demo:1.0.0") {
    env("SPRING_PROFILES_ACTIVE") {
        fromValue("production")
    }

    envFrom {
        configMapRef("demo-config") {
            optional = false
        }
        secretRef("demo-secret") {
            optional = true
        }
    }
}
```

`env`는 개별 변수를 명시적으로 설정합니다. `envFrom`은 외부 소스에서 여러 변수를 가져옵니다.

## 리소스

```kotlin
container("app", "registry.example.com/demo:1.0.0") {
    resources {
        requests {
            cpu = 250.mCpu
            memory = 256.miBytes
        }
        limits {
            cpu = oneCpu
            memory = 1.giBytes
            ephemeralStorage = 2.giBytes
        }
    }
}
```

`requests`는 계획된 최소 리소스 양을 기술합니다. `limits`는 상한을 기술합니다. DSL은 limits가 requests보다 작지 않은지 검증합니다.

## 볼륨 마운트

```kotlin
container("app", "registry.example.com/demo:1.0.0") {
    volumeMounts {
        volumeMount("config", "/etc/demo") {
            readOnly = true
        }
        volumeMount("data", "/var/lib/demo")
    }
}
```

`volumeMount`의 이름은 Pod Spec 내의 볼륨과 일치해야 합니다.
