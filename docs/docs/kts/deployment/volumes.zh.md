# 卷

卷在 Pod Spec 中定义，然后通过 `volumeMounts` 或 `volumeDevices` 在容器中使用。

```kotlin
spec {
    volumes {
        volume("config") {
            from {
                configMap {
                    name = "demo-config"
                }
            }
        }
    }

    containers {
        container("app", "registry.example.com/demo:1.0.0") {
            volumeMounts {
                volumeMount("config", "/etc/demo") {
                    readOnly = true
                }
            }
        }
    }
}
```

## ConfigMap

```kotlin
volumes {
    volume("config") {
        fromConfigMap {
            name = "demo-config"
            optional = false
            defaultMode = 420

            items {
                item("application.yaml", "application.yaml") {
                    mode = 420
                }
            }
        }
    }
}
```

## Secret

```kotlin
volumes {
    volume("credentials") {
        from {
            secret {
                name = "demo-secret"
                optional = false
                defaultMode = 256
            }
        }
    }
}
```

## PersistentVolumeClaim

```kotlin
volumes {
    volume("data") {
        fromPersistentVolumeClaim("demo-data") {
            readOnly = false
        }
    }
}
```

## HostPath

```kotlin
volumes {
    volume("host-logs") {
        fromHostPath("/var/log/demo") {
            type = VolumeSpec.HostPathSourceSpec.Type.DirectoryOrCreate
        }
    }
}
```

HostPath 将节点上的文件系统路径绑定到 Pod 中。这会使 Pod 与节点环境紧密耦合，应仅在有意为之时使用。

## EmptyDir

```kotlin
volumes {
    volume("cache") {
        emptyDir {
            medium = VolumeSpec.EmptyDirSourceSpec.MediumType.Memory
            sizeLimit = 512.miBytes
        }
    }
}
```

`emptyDir` 与 Pod 共存亡。当创建新的 Pod 时，该卷会被重新创建。

## 挂载与设备

```kotlin
container("app", "registry.example.com/demo:1.0.0") {
    volumeMounts {
        volumeMount("cache", "/cache")
        volumeMount("credentials", "/run/secrets") {
            readOnly = true
        }
    }

    volumeDevices {
        volumeDevice("block-data", "/dev/xvda")
    }
}
```

`volumeMounts` 用于文件系统挂载。`volumeDevices` 用于块设备。
