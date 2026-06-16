# Pod Spec

Pod Spec は、Pod 内のすべてのコンテナのランタイム、ネットワーク、スケジューリング、セキュリティのオプションを記述します。Deployment では、これは `template` の一部です。

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

## コアプロパティ

| プロパティ / メソッド | 説明 |
| :--- | :--- |
| `containers { container(name, image) { ... } }` | 必須のメインコンテナのリスト。 |
| `containers { init(name, image) { ... } }` | メインコンテナの前に実行される Init コンテナ。 |
| `containers { ephemeral(name, image) { ... } }` | デバッグ用途のエフェメラルコンテナ。 |
| `restartPolicy` | コンテナの再起動動作: `Always`、`OnFailure`、`Never`。 |
| `serviceAccountName` | Pod の実行に使用する ServiceAccount。 |
| `automountServiceAccountToken` | ServiceAccount トークンを自動的にマウントするかどうかを制御します。 |
| `imagePullSecrets { secret(name) }` | プライベートコンテナレジストリ用の Secret。 |
| `volumes { volume(name) { ... } }` | コンテナがマウントできるボリューム。 |
| `nodeSelector { select(key, value) }` | ラベルによる単純なノード選択。 |
| `affinity { ... }` | スケジューリングのためのアフィニティおよびアンチアフィニティルール。 |
| `tolerations { toleration { ... } }` | 一致する taint を持つノードへのスケジューリングを許可します。 |
| `topologySpreadConstraints { constraint(...) { ... } }` | トポロジードメイン間での Pod の分散。 |
| `securityContext { ... }` | Pod レベルのセキュリティコンテキスト。 |
| `terminationGracePeriodSeconds` | Pod を終了する際の猶予期間。 |
| `activeDeadlineSeconds` | Pod の最大ライフタイム。 |

## ネットワークと DNS

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

その他のネットワークオプションには `hostNetwork`、`hostPID`、`hostIPC`、`hostname`、`subdomain`、`setHostnameAsFQDN` があります。

## スケジューリング

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

## セキュリティコンテキスト

Pod レベルの `securityContext { ... }` は Pod 全体に適用されます。コンテナはさらに独自のセキュリティコンテキストを定義できます。

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
