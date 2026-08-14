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
    env("DB_PASSWORD") {
        fromSecretKeyReference("demo-secret", "password")
    }

    envs {
        variable("LOG_LEVEL") {
            fromValue("debug")
        }
    }

    envFrom {
        configMapRef("demo-config") {
            optional = false
        }
    }
    envFrom {
        secretRef("demo-secret") {
            optional = true
        }
    }
}
```

`env`는 여러 번 호출하여 여러 변수를 정의할 수 있으며, `envs { }`는 이를 하나의 블록으로 묶습니다. `envFrom`은 호출할 때마다 소스를 하나씩 추가하고, `envsFrom { }`으로 묶을 수 있습니다. 둘 다 YAML 목록으로 렌더링됩니다.

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
        volumeMount("config", "/etc/demo/application.yaml") {
            readOnly = true
            subPath = "application.yaml"
        }
        volumeMount("data", "/var/lib/demo")
    }
}
```

`volumeMount`의 이름은 Pod Spec의 볼륨과 일치해야 합니다. `subPath`는 볼륨의 루트가 아닌 단일 파일 또는 디렉터리를 마운트하며, `subPathExpr`도 같은 역할을 하지만 환경 변수를 참조할 수 있습니다.

## 사이드카와 리사이즈

```kotlin
containers {
    init("proxy", "registry.example.com/envoy:1.0.0") {
        restartPolicy = ContainerSpec.RestartPolicy.Always
    }

    container("app", "registry.example.com/demo:1.0.0") {
        addResizePolicy(
            ResourceResizePolicySpec.ResourceName.Cpu,
            ResourceResizePolicySpec.RestartPolicy.NotRequired
        )
        resources {
            addClaim("gpu")
        }
    }
}
```

`restartPolicy = Always`를 설정한 init 컨테이너는 메인 컨테이너와 함께 계속 실행되는 네이티브 사이드카가 됩니다. `addResizePolicy`는 리소스를 제자리에서 변경할 때 재시작이 필요한지 선언하고, `addClaim`은 Pod에 선언된 리소스 클레임을 참조합니다.
