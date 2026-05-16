# Pod Spec

The Pod Spec describes runtime, network, scheduling, and security options for all containers in the Pod. In Deployments, it is part of the `template`.

```kotlin
template {
    spec {
        serviceAccountName = "demo-service-account"
        restartPolicy = PodSpec.RestartPolicy.Always
        dnsPolicy = PodSpec.DnsPolicy.ClusterFirst

        nodeSelector {
            select("kubernetes.io/os", "linux")
        }

        imagePullSecrets {
            secret("registry-credentials")
        }

        containers {
            container("app", "nginx:1.27") {}
        }
    }
}
```

## Core Properties

| Property / Method | Description |
| :--- | :--- |
| `containers { container(name, image) { ... } }` | Required list of main containers. |
| `containers { init(name, image) { ... } }` | Init containers that run before the main containers. |
| `containers { ephemeral(name, image) { ... } }` | Ephemeral containers for debugging scenarios. |
| `restartPolicy` | Container restart behavior: `Always`, `OnFailure`, `Never`. |
| `serviceAccountName` | ServiceAccount used to run the Pod. |
| `automountServiceAccountToken` | Controls whether the ServiceAccount token is mounted automatically. |
| `imagePullSecrets { secret(name) }` | Secrets for private container registries. |
| `volumes { volume(name) { ... } }` | Volumes that containers can mount. |
| `nodeSelector { select(key, value) }` | Simple node selection through labels. |
| `affinity { ... }` | Affinity and anti-affinity rules for scheduling. |
| `tolerations { toleration { ... } }` | Allows scheduling on nodes with matching taints. |
| `topologySpreadConstraints { constraint(...) { ... } }` | Distribution of Pods across topology domains. |
| `securityContext { ... }` | Security context at Pod level. |
| `terminationGracePeriodSeconds` | Grace period for terminating the Pod. |
| `activeDeadlineSeconds` | Maximum lifetime of the Pod. |

## Network and DNS

```kotlin
spec {
    dnsPolicy = PodSpec.DnsPolicy.ClusterFirst

    dnsConfig {
        nameservers {
            nameserver("10.96.0.10")
        }
        searches {
            search("svc.cluster.local")
        }
        options {
            option("ndots", "2")
        }
    }

    containers {
        container("app", "nginx:1.27") {}
    }
}
```

Additional network options include `hostNetwork`, `hostPID`, `hostIPC`, `hostname`, `subdomain`, and `setHostnameAsFQDN`.

## Scheduling

```kotlin
spec {
    priorityClassName = "high-priority"
    schedulerName = "default-scheduler"

    nodeSelector {
        select("nodepool", "apps")
    }

    tolerations {
        toleration {
            key = "dedicated"
            value = "apps"
            effect = TolerationSpec.Effect.NoSchedule
        }
    }

    containers {
        container("app", "nginx:1.27") {}
    }
}
```

## Security Context

`securityContext { ... }` at Pod level applies to the Pod as a whole. Containers can additionally define their own security contexts.

```kotlin
spec {
    securityContext {
        runAsUser = 1000
        runAsGroup = 1000
        runAsNonRoot = true
    }

    containers {
        container("app", "nginx:1.27") {}
    }
}
```
