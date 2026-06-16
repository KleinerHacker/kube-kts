# Pod Spec

Pod Spec 描述了 Pod 中所有容器的运行时、网络、调度和安全选项。在 Deployment 中，它是 `template` 的一部分。

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

## 核心属性

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `containers { container(name, image) { ... } }` | 必需的主容器列表。 |
| `containers { init(name, image) { ... } }` | 在主容器之前运行的 Init 容器。 |
| `containers { ephemeral(name, image) { ... } }` | 用于调试场景的临时容器。 |
| `restartPolicy` | 容器重启行为：`Always`、`OnFailure`、`Never`。 |
| `serviceAccountName` | 用于运行 Pod 的 ServiceAccount。 |
| `automountServiceAccountToken` | 控制是否自动挂载 ServiceAccount 令牌。 |
| `imagePullSecrets { secret(name) }` | 用于私有容器镜像仓库的 Secret。 |
| `volumes { volume(name) { ... } }` | 容器可挂载的卷。 |
| `nodeSelector { select(key, value) }` | 通过标签进行简单的节点选择。 |
| `affinity { ... }` | 用于调度的亲和性与反亲和性规则。 |
| `tolerations { toleration { ... } }` | 允许调度到带有匹配污点的节点上。 |
| `topologySpreadConstraints { constraint(...) { ... } }` | Pod 在拓扑域间的分布。 |
| `securityContext { ... }` | Pod 级别的安全上下文。 |
| `terminationGracePeriodSeconds` | 终止 Pod 的宽限期。 |
| `activeDeadlineSeconds` | Pod 的最长生命周期。 |

## 网络与 DNS

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

其他网络选项包括 `hostNetwork`、`hostPID`、`hostIPC`、`hostname`、`subdomain` 和 `setHostnameAsFQDN`。

## 调度

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

## 安全上下文

Pod 级别的 `securityContext { ... }` 应用于整个 Pod。容器还可以额外定义自己的安全上下文。

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
