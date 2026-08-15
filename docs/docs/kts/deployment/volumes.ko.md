# 볼륨

볼륨은 Pod Spec에서 정의한 후 `volumeMounts` 또는 `volumeDevices`를 통해 컨테이너에서 사용합니다.

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
            type = HostPathSourceSpec.Type.DirectoryOrCreate
        }
    }
}
```

HostPath는 노드의 파일 시스템 경로를 Pod에 바인딩합니다. 이는 Pod를 노드 환경에 강하게 결합시키므로 의도적으로만 사용해야 합니다.

## EmptyDir

```kotlin
volumes {
    volume("cache") {
        emptyDir {
            medium = EmptyDirSourceSpec.MediumType.Memory
            sizeLimit = 512.miBytes
        }
    }
}
```

`emptyDir`는 Pod가 존재하는 동안 유지됩니다. 새 Pod가 생성되면 볼륨도 다시 생성됩니다.

## 마운트와 디바이스

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

`volumeMounts`는 파일 시스템 마운트에 사용합니다. `volumeDevices`는 블록 디바이스에 사용합니다.

## 모든 볼륨 소스

위에 소개한 소스 외에도 이 DSL은 Kubernetes의 모든 볼륨 소스를 지원합니다.

| Group      | Sources                                                                                                      |
|------------|--------------------------------------------------------------------------------------------------------------|
| Config     | `configMap`, `secret`, `projected`, `downwardApi`                                                            |
| Node-local | `emptyDir`, `hostPath`, `persistentVolumeClaim`, `ephemeral`, `image`, `csi`                                 |
| Network    | `nfs`, `iscsi`, `fibreChannel`, `rbd`, `cephFs`, `glusterFs`                                                 |
| Cloud      | `awsElasticBlockStore`, `gcePersistentDisk`, `azureDisk`, `azureFile`, `cinder`, `portworx`, `vsphereVolume` |

Kubernetes에서 제거된 소스 (`gitRepo`, `flexVolume`, `flocker`, `quobyte`, `scaleIo`, `storageOs`, `photonPersistentDisk`)는 이전
클러스터를 위해 계속 사용할 수 있지만 더 이상 사용되지 않는 것으로 표시됩니다.

### 예시

```kotlin
volumes {
    volume("bundle") {
        from {
            projected {
                addConfigMap { name = "demo-config" }
                addSecret { name = "demo-secret" }
                addServiceAccountToken("token")
            }
        }
    }

    volume("podinfo") {
        from {
            downwardApi {
                addFieldRef("labels", "metadata.labels")
                addResourceFieldRef("cpu_limit", "limits.cpu", containerName = "app")
            }
        }
    }

    volume("data") {
        from {
            csi("ebs.csi.aws.com") {
                fsType = "ext4"
                addVolumeAttribute("encrypted", "true")
            }
        }
    }

    volume("scratch") {
        from {
            ephemeral {
                spec {
                    accessModes(VolumeClaimTemplateSpec.AccessMode.ReadWriteOnce)
                    requests(1.giBytes)
                }
            }
        }
    }
}
```
