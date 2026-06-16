# Deployment Selector

`selector` 定义 Deployment 管理哪些 Pod。在 DSL 中，此块是必需的。至少必须设置一个 `matchLabels` 条目。

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

`matchLabels` 描述简单的键值条件。只有当所有指定的标签都存在且其值匹配时，Pod 才会匹配。

```kotlin
selector {
    matchLabels {
        label("app", "demo")
        label("component", "api")
    }
}
```

## Match Expressions

`matchExpressions` 允许使用操作符表达更复杂的条件。

| 操作符 | 含义 |
| :--- | :--- |
| `In` | 标签值必须包含在指定的值列表中。 |
| `NotIn` | 标签值不得包含在指定的值列表中。 |
| `Exists` | 标签必须存在。 |
| `DoesNotExist` | 标签不得存在。 |

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

## 与 Pod 模板的关系

`template.metadata` 中的标签必须满足 selector。

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

如果 selector 和 template 不匹配，Kubernetes 将无法正确管理该 Deployment。
