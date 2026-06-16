# Deployment Selector

`selector` は、Deployment がどの Pod を管理するかを定義します。DSL では、このブロックは必須です。少なくとも 1 つの `matchLabels` エントリを設定する必要があります。

```kotlin
spec {
    selector {
        matchLabels {
            label("app", "demo")
        }

        matchExpressions {
            expression("tier", LabelSelectorRequirementSpec.Operator.In) {
                values {
                    value("backend")
                }
            }
        }
    }
}
```

## Match Labels

`matchLabels` は単純なキーと値の条件を記述します。指定したすべてのラベルが存在し、その値が一致する場合にのみ Pod が一致します。

```kotlin
selector {
    matchLabels {
        label("app", "demo")
        label("component", "api")
    }
}
```

## Match Expressions

`matchExpressions` を使うと、演算子を用いたより複雑な条件を指定できます。

| 演算子 | 意味 |
| :--- | :--- |
| `In` | ラベルの値が指定した値のリストに含まれている必要があります。 |
| `NotIn` | ラベルの値が指定した値のリストに含まれていてはいけません。 |
| `Exists` | ラベルが存在する必要があります。 |
| `DoesNotExist` | ラベルが存在してはいけません。 |

```kotlin
selector {
    matchLabels {
        label("app", "demo")
    }

    matchExpressions {
        expression("environment", LabelSelectorRequirementSpec.Operator.In) {
            values {
                value("production")
                value("staging")
            }
        }
        expression("deprecated", LabelSelectorRequirementSpec.Operator.DoesNotExist)
    }
}
```

## Pod テンプレートとの関係

`template.metadata` のラベルは selector を満たす必要があります。

```kotlin
selector {
    matchLabels {
        label("app", "demo")
    }
}

template {
    metadata {
        labels {
            label("app", "demo")
        }
    }
    spec {
        containers {
            container("app", "nginx:1.27") {}
        }
    }
}
```

selector と template が一致しない場合、Kubernetes は Deployment を正しく管理できません。
