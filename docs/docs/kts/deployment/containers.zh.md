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

`env` 用于显式设置单个变量。`envFrom` 用于从外部来源导入多个变量。

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
        volumeMount("config", "/etc/demo") {
            readOnly = true
        }
        volumeMount("data", "/var/lib/demo")
    }
}
```

`volumeMount` 中的名称必须与 Pod Spec 中的某个卷匹配。
