# StatefulSet DSL

The `statefulSet` DSL is used to configure Kubernetes **StatefulSet** resources. A StatefulSet manages
a set of Pods with stable, unique network identities and stable, persistent per-Pod storage — suitable
for stateful applications such as databases or message brokers, as opposed to the interchangeable Pods
managed by a [Deployment](deployment.md).

!!! warning "Security: Import Restrictions"
    By default, KTS scripts **do not allow** `import` statements or fully qualified class names
    (e.g. `java.lang.Runtime`). Only types provided via the pre-configured default imports may
    be used.

    Use the `--unsafe` flag to lift these restrictions.

## Basic Usage

A minimal StatefulSet requires `metadata`, a `serviceName` (the governing headless Service), a
`selector` and a Pod `template` in `spec`.

```kotlin
statefulSet {
    metadata("my-database") {
        namespace = "default"
    }

    spec {
        serviceName = "my-database"

        selector {
            matchLabels {
                label("app", "my-database")
            }
        }

        template {
            metadata {
                labels {
                    label("app", "my-database")
                }
            }

            spec {
                containers {
                    container("db", "postgres") { }
                }
            }
        }
    }
}
```

## Detailed Example

The following is a comprehensive example showing replicas, the governing service, Pod management,
an update strategy, per-Pod persistent storage via `volumeClaimTemplates` and a claim retention policy.

```kotlin
statefulSet {
    metadata("full-database") {
        namespace = "data"
    }

    spec {
        replicas = 3
        serviceName = "full-database"
        podManagementPolicy = StatefulSetSpec.PodManagementPolicy.Parallel
        revisionHistoryLimit = 5
        minReadySeconds = 10.seconds.toJavaDuration()
        ordinals(1)

        // Reuses the shared label selector DSL (same as Deployment)
        selector {
            matchLabels {
                label("app", "demo")
            }
        }

        // Control how template updates are rolled out
        updateStrategy {
            type = StatefulSetUpdateStrategySpec.Type.RollingUpdate
            rollingUpdate {
                partition = 1
                maxUnavailable = 1.absolute
            }
        }

        // Stable per-Pod storage: one PVC per Pod is provisioned per template
        volumeClaimTemplates {
            claim("data") {
                accessModes(VolumeClaimTemplateSpec.AccessMode.ReadWriteOnce)
                storageClassName = "standard"
                volumeMode = VolumeClaimTemplateSpec.VolumeMode.Filesystem
                requests {
                    storage = 1.giBytes
                }
            }
        }

        // Decide what happens to the PVCs on delete / scale down
        persistentVolumeClaimRetentionPolicy {
            whenDeleted = PersistentVolumeClaimRetentionPolicySpec.RetentionPolicyType.Delete
            whenScaled = PersistentVolumeClaimRetentionPolicySpec.RetentionPolicyType.Retain
        }

        template {
            metadata {
                labels {
                    label("app", "demo")
                }
            }

            spec {
                containers {
                    container("db", "postgres") { }
                }
            }
        }
    }
}
```

## Configuration Reference

### Metadata (`metadata`)

| Property | Type | Description |
| :--- | :--- | :--- |
| `name` | `String` | The name of the StatefulSet resource (passed as the first argument). |
| `namespace` | `String?` | The namespace for the resource. |
| `generateName` | `String?` | An optional prefix for generating a unique name. |

### StatefulSet Specification (`spec`)

| Property / Method | Description |
| :--- | :--- |
| `replicas` | The desired number of replicas (Pod instances). Defaults to 1. |
| `serviceName` | The name of the governing (usually headless) Service. **Required.** |
| `podManagementPolicy` | `OrderedReady` (default) or `Parallel`. |
| `revisionHistoryLimit` | The maximum number of revisions to retain for rollback. |
| `minReadySeconds` | Minimum seconds a new Pod must be ready to be considered available. |
| `ordinals(start)` | The number representing the first replica index (defaults to 0). |
| `selector { ... }` | The label selector (reuses the shared selector DSL, see [Selector](deployment/selector.md)). **Required.** |
| `updateStrategy { ... }` | Controls how template updates are rolled out. |
| `volumeClaimTemplates { claim(name) { ... } }` | PersistentVolumeClaim templates providing stable per-Pod storage. |
| `persistentVolumeClaimRetentionPolicy { ... }` | Controls the lifecycle of the provisioned PVCs. |
| `template { ... }` | The Pod template (see [Pod Template](deployment/template.md)). **Required.** |

### Update Strategy (`updateStrategy`)

| Property / Method | Description |
| :--- | :--- |
| `type` | `RollingUpdate` (default) or `OnDelete`. |
| `rollingUpdate { partition; maxUnavailable }` | The rolling update parameters. |
| `partition` | The ordinal at and above which Pods are updated (staged/canary rollouts). |
| `maxUnavailable` | Max Pods unavailable during the update (absolute or percentage, e.g. `1.absolute` / `25.percent`). |

### Volume Claim Templates (`volumeClaimTemplates`)

| Property / Method | Description |
| :--- | :--- |
| `claim(name) { ... }` | Adds a PVC template. `name` must match a `volumeMount` of the Pod's containers. |
| `accessModes(...)` | The access modes, e.g. `ReadWriteOnce`, `ReadOnlyMany`, `ReadWriteMany`, `ReadWriteOncePod`. |
| `storageClassName` | The StorageClass used to provision the volume. |
| `volumeMode` | `Filesystem` (default) or `Block`. |
| `requests { storage = ... }` | The minimum requested storage (e.g. `1.giBytes`). |
| `limits { storage = ... }` | The maximum allowed storage. |
| `label(key, value)` / `labels { ... }` | Labels of the claim metadata. |
| `annotation(key, value)` / `annotations { ... }` | Annotations of the claim metadata. |

### PVC Retention Policy (`persistentVolumeClaimRetentionPolicy`)

| Property | Description |
| :--- | :--- |
| `whenDeleted` | `Retain` (default) or `Delete` — applied when the StatefulSet is deleted. |
| `whenScaled` | `Retain` (default) or `Delete` — applied when the StatefulSet is scaled down. |

!!! note "Stable storage"
    Each entry of `volumeClaimTemplates` provisions one PersistentVolumeClaim **per Pod**. The claims
    keep their identity across rescheduling, giving every replica its own durable storage.
