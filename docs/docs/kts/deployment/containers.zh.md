# 容器

容器在 Pod Spec 的 `containers` 块中定义。每个主容器都需要一个名称和一个镜像。

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

## 核心属性

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `imagePullPolicy` | 镜像拉取行为：`Always`、`IfNotPresent`、`Never`。 |
| `ports { port(containerPort) { ... } }` | 容器端口，可带可选的名称和协议。 |
| `env(name) { ... }` | 单个环境变量。 |
| `envFrom { ... }` | 来自 ConfigMap 或 Secret 的环境变量。 |
| `resources { requests { ... } limits { ... } }` | CPU、内存和存储的请求与限制。 |
| `volumeMounts { volumeMount(name, mountPath) { ... } }` | Pod 卷的挂载点。 |
| `livenessProbe { ... }` | 检查容器是否必须被重启。 |
| `readinessProbe { ... }` | 检查容器是否可以接收流量。 |
| `startupProbe { ... }` | 针对较长启动阶段的检查。 |
| `lifecycle { ... }` | 生命周期钩子，例如 `postStart` 和 `preStop`。 |
| `securityContext { ... }` | 容器级别的安全选项。 |
| `command(...)` | 覆盖镜像的 entrypoint。 |
| `args(...)` | 覆盖或追加镜像参数。 |
| `workingDir` | 容器内的工作目录。 |

## 端口

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

命名端口随后可以被 Service 或 Probe 等引用。

## 环境变量

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

`env` 可多次调用以定义多个变量，`envs { }` 可将它们归入一个代码块。每次调用 `envFrom` 会添加一个来源，`envsFrom { }` 可将它们归组。两者都渲染为 YAML 列表。

## 资源

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

`requests` 描述计划的最小资源量。`limits` 描述上限。DSL 会验证 limits 不低于 requests。

## 卷挂载

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

`volumeMount` 中的名称必须与 Pod Spec 中的卷一致。`subPath` 挂载卷中的单个文件或目录而非其根目录；`subPathExpr` 作用相同，但可以引用环境变量。

## 边车容器与资源调整

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

设置了 `restartPolicy = Always` 的 init 容器会成为原生边车容器，与主容器一同持续运行。`addResizePolicy` 声明原地调整资源时是否需要重启，`addClaim` 引用 Pod 上声明的资源申领。
