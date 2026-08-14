# ボリューム

ボリュームは Pod Spec で定義し、その後 `volumeMounts` または `volumeDevices` を通じてコンテナで使用します。

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

HostPath はノードのファイルシステムパスを Pod にバインドします。これは Pod をノード環境に密接に結びつけるため、意図した場合にのみ使用してください。

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

`emptyDir` は Pod が存在する限り存続します。新しい Pod が作成されると、ボリュームも再作成されます。

## マウントとデバイス

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

`volumeMounts` はファイルシステムマウントに使用します。`volumeDevices` はブロックデバイスに使用します。

## すべてのボリュームソース

上記のソースに加えて、この DSL は Kubernetes のすべてのボリュームソースに対応しています。

| Group | Sources |
|-------|---------|
| Config | `configMap`, `secret`, `projected`, `downwardApi` |
| Node-local | `emptyDir`, `hostPath`, `persistentVolumeClaim`, `ephemeral`, `image`, `csi` |
| Network | `nfs`, `iscsi`, `fibreChannel`, `rbd`, `cephFs`, `glusterFs` |
| Cloud | `awsElasticBlockStore`, `gcePersistentDisk`, `azureDisk`, `azureFile`, `cinder`, `portworx`, `vsphereVolume` |

Kubernetes から削除されたソース（`gitRepo`、`flexVolume`、`flocker`、`quobyte`、`scaleIo`、`storageOs`、`photonPersistentDisk`）は、古いクラスター向けに引き続き利用できますが、非推奨としてマークされています。

### 例

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
