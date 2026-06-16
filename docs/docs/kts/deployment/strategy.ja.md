# Deployment ストラテジー

`strategy` は、Deployment が変更されたときに Kubernetes がどのように Pod を置き換えるかを制御します。

```kotlin
strategy {
    type = DeploymentStrategySpec.Type.RollingUpdate
    rollingUpdate {
        maxSurge = 25.percent
        maxUnavailable = 1.absolute
    }
}
```

## Recreate

`Recreate` では、Kubernetes は新しい Pod を作成する前に既存の Pod を終了します。

```kotlin
strategy {
    type = DeploymentStrategySpec.Type.Recreate
}
```

このストラテジーは単純ですが、ダウンタイムが発生する可能性があります。新旧のバージョンを並行して実行してはいけないアプリケーションに適しています。

## RollingUpdate

`RollingUpdate` では、Kubernetes は Pod を段階的に置き換えます。

```kotlin
strategy {
    type = DeploymentStrategySpec.Type.RollingUpdate
    rollingUpdate {
        maxSurge = 1.absolute
        maxUnavailable = 0.absolute
    }
}
```

| プロパティ | 説明 |
| :--- | :--- |
| `maxSurge` | `replicas` を超えて追加できる Pod の最大数。 |
| `maxUnavailable` | ロールアウト中に利用不可となる Pod の最大数。 |

どちらの値も絶対値またはパーセンテージで指定できます。

```kotlin
rollingUpdate {
    maxSurge = 25.percent
    maxUnavailable = 10.percent
}
```

```kotlin
rollingUpdate {
    maxSurge = 2.absolute
    maxUnavailable = 1.absolute
}
```

## Deployment Spec のロールアウトオプション

| プロパティ | 説明 |
| :--- | :--- |
| `minReadySeconds` | 新しい Pod が利用可能とみなされる前に Ready 状態を維持しなければならない最小時間。 |
| `revisionHistoryLimit` | ロールバックのために保持する古い ReplicaSet の数。 |
| `paused` | `true` の場合、Deployment コントローラーは新しいロールアウト変更を処理しません。 |
| `progressDeadlineSeconds` | ロールアウトの進行に対する時間制限。 |

```kotlin
spec {
    replicas = 3
    minReadySeconds = 30.seconds.toJavaDuration()
    revisionHistoryLimit = 5
    paused = false
    progressDeadlineSeconds = 600.seconds.toJavaDuration()

    strategy {
        type = DeploymentStrategySpec.Type.RollingUpdate
        rollingUpdate {
            maxSurge = 25.percent
            maxUnavailable = 1.absolute
        }
    }
}
```
