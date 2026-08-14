# Volumes

Volumes are defined in the Pod Spec and then used in containers through `volumeMounts` or `volumeDevices`.

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

HostPath binds filesystem paths from the node into the Pod. This couples Pods tightly to the node environment and should only be used intentionally.

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

`emptyDir` lives for as long as the Pod exists. When a new Pod is created, the volume is created again.

## Mounts and Devices

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

`volumeMounts` are used for filesystem mounts. `volumeDevices` are used for block devices.

## All Volume Sources

Beyond the sources shown above, the DSL covers the complete set of Kubernetes volume sources:

| Group | Sources |
|-------|---------|
| Config | `configMap`, `secret`, `projected`, `downwardApi` |
| Node-local | `emptyDir`, `hostPath`, `persistentVolumeClaim`, `ephemeral`, `image`, `csi` |
| Network | `nfs`, `iscsi`, `fibreChannel`, `rbd`, `cephFs`, `glusterFs` |
| Cloud | `awsElasticBlockStore`, `gcePersistentDisk`, `azureDisk`, `azureFile`, `cinder`, `portworx`, `vsphereVolume` |

Sources that Kubernetes has removed (`gitRepo`, `flexVolume`, `flocker`, `quobyte`, `scaleIo`, `storageOs`, `photonPersistentDisk`) remain available for older clusters but are marked deprecated.

### Examples

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
