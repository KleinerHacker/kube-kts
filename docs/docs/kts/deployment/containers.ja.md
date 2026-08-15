# コンテナ

コンテナは Pod Spec の `containers` ブロックで定義します。各メインコンテナには名前とイメージが必要です。

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

## コアプロパティ

| プロパティ / メソッド                                   | 説明                                                     |
|:--------------------------------------------------------|:---------------------------------------------------------|
| `imagePullPolicy`                                       | イメージのプル動作: `Always`、`IfNotPresent`、`Never`。  |
| `ports { port(containerPort) { ... } }`                 | 任意の名前とプロトコルを持つコンテナポート。             |
| `env(name) { ... }`                                     | 単一の環境変数。                                         |
| `envFrom { ... }`                                       | ConfigMap または Secret からの環境変数。                 |
| `resources { requests { ... } limits { ... } }`         | CPU、メモリ、ストレージのリクエストとリミット。          |
| `volumeMounts { volumeMount(name, mountPath) { ... } }` | Pod ボリュームのマウント。                               |
| `livenessProbe { ... }`                                 | コンテナを再起動すべきかどうかを確認します。             |
| `readinessProbe { ... }`                                | コンテナがトラフィックを受け取れるかどうかを確認します。 |
| `startupProbe { ... }`                                  | 長い起動フェーズのためのチェック。                       |
| `lifecycle { ... }`                                     | `postStart` や `preStop` などのライフサイクルフック。    |
| `securityContext { ... }`                               | コンテナレベルのセキュリティオプション。                 |
| `command(...)`                                          | イメージの entrypoint を上書きします。                   |
| `args(...)`                                             | イメージの引数を上書きまたは追加します。                 |
| `workingDir`                                            | コンテナ内の作業ディレクトリ。                           |

## ポート

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

名前付きポートは、後で Service やプローブなどから参照できます。

## 環境変数

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

`env` は複数回呼び出して複数の変数を定義でき、`envs { }` はそれらを 1 つのブロックにまとめます。`envFrom` は呼び出すたびにソースを
1 つ追加し、`envsFrom { }` でまとめられます。どちらも YAML のリストとして出力されます。

## リソース

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

`requests` は計画された最小リソース量を記述します。`limits` は上限を記述します。DSL は limits が requests を下回らないことを検証します。

## ボリュームマウント

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

`volumeMount` の名前は Pod Spec のボリュームと一致する必要があります。`subPath` はボリュームのルートではなく単一のファイルまたはディレクトリをマウントします。
`subPathExpr` も同様ですが、環境変数を参照できます。

## サイドカーとリサイズ

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

`restartPolicy = Always` を設定した init コンテナは、メインコンテナと並行して動作し続けるネイティブサイドカーになります。
`addResizePolicy` はリソースのインプレース変更に再起動が必要かどうかを宣言し、`addClaim` は Pod で宣言されたリソースクレームを参照します。
