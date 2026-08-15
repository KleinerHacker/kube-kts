# Containers

Containers are defined in the `containers` block of the Pod Spec. Each main container requires a name and an image.

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

## Core Properties

| Property / Method                                       | Description                                                     |
|:--------------------------------------------------------|:----------------------------------------------------------------|
| `imagePullPolicy`                                       | Pull behavior for the image: `Always`, `IfNotPresent`, `Never`. |
| `ports { port(containerPort) { ... } }`                 | Container ports with optional name and protocol.                |
| `env(name) { ... }`                                     | Single environment variable.                                    |
| `envFrom { ... }`                                       | Environment variables from ConfigMaps or Secrets.               |
| `resources { requests { ... } limits { ... } }`         | CPU, memory, and storage requests and limits.                   |
| `volumeMounts { volumeMount(name, mountPath) { ... } }` | Mounts for Pod volumes.                                         |
| `livenessProbe { ... }`                                 | Checks whether the container must be restarted.                 |
| `readinessProbe { ... }`                                | Checks whether the container may receive traffic.               |
| `startupProbe { ... }`                                  | Check for long startup phases.                                  |
| `lifecycle { ... }`                                     | Lifecycle hooks such as `postStart` and `preStop`.              |
| `securityContext { ... }`                               | Security options at container level.                            |
| `command(...)`                                          | Overrides the image entrypoint.                                 |
| `args(...)`                                             | Overrides or adds image arguments.                              |
| `workingDir`                                            | Working directory inside the container.                         |

## Ports

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

Named ports can later be referenced by Services or Probes, for example.

## Environment

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

`env` can be called several times to define multiple variables, and `envs { }` groups them in one block. Each `envFrom`
call adds one further source; `envsFrom { }` groups them. Both are rendered as YAML lists.

## Resources

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

`requests` describes the planned minimum amount of resources. `limits` describes the upper bound. The DSL validates that
limits are not lower than requests.

## Volume Mounts

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

The name in `volumeMount` must match a volume in the Pod Spec. `subPath` mounts a single file or directory of the volume
instead of its root; `subPathExpr` does the same but may reference environment variables.

## Sidecars and Resizing

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

An init container with `restartPolicy = Always` becomes a native sidecar that keeps running alongside the main
containers. `addResizePolicy` declares whether an in-place resource change requires a restart, and `addClaim` references
a resource claim declared on the Pod.
