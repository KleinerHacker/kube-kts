# プローブ

プローブはコンテナ上に定義します。Kubernetes がアプリケーションの状態を評価するのに役立ちます。

| プローブ | 目的 |
| :--- | :--- |
| `startupProbe` | アプリケーションが起動したかどうかを確認します。成功するまで他のプローブは実行されません。 |
| `readinessProbe` | コンテナがトラフィックを受け取れるかどうかを確認します。失敗すると Pod が Service エンドポイントから除外されます。 |
| `livenessProbe` | コンテナがまだ正常に機能しているかどうかを確認します。失敗すると再起動が発生する可能性があります。 |

## HTTP GET

```kotlin
container("app", "registry.example.com/demo:1.0.0") {
    readinessProbe {
        httpGet(8080) {
            path = "/actuator/health/readiness"
            scheme = ProtocolScheme.HTTP
            httpHeaders {
                httpHeader("X-Probe", "readiness")
            }
        }
        initialDelaySeconds = 10.seconds.toJavaDuration()
        periodSeconds = 10.seconds.toJavaDuration()
        timeoutSeconds = 2.seconds.toJavaDuration()
        failureThreshold = 3
    }
}
```

## TCP Socket

```kotlin
container("app", "registry.example.com/demo:1.0.0") {
    livenessProbe {
        tcpSocket(8080)
        initialDelaySeconds = 30.seconds.toJavaDuration()
        periodSeconds = 20.seconds.toJavaDuration()
    }
}
```

## Exec

```kotlin
container("worker", "registry.example.com/worker:1.0.0") {
    livenessProbe {
        exec {
            command("/bin/sh", "-c", "test -f /tmp/healthy")
        }
        periodSeconds = 15.seconds.toJavaDuration()
    }
}
```

## gRPC

```kotlin
container("grpc-api", "registry.example.com/grpc-api:1.0.0") {
    readinessProbe {
        grpc(9090) {
            service = "demo.Health"
        }
        periodSeconds = 10.seconds.toJavaDuration()
    }
}
```

## タイミングオプション

| プロパティ | 説明 |
| :--- | :--- |
| `initialDelaySeconds` | コンテナ起動後、最初のチェックまでの待機時間。 |
| `periodSeconds` | 2 回のチェックの間隔。 |
| `timeoutSeconds` | 単一チェックの最大時間。 |
| `successThreshold` | 成功ステータスに切り替えるために必要な連続成功回数。 |
| `failureThreshold` | 失敗処理が開始されるまでの連続失敗回数。 |
| `terminationGracePeriodSeconds` | プローブによって発生する終了のための猶予期間。 |
