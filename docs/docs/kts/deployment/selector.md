# Deployment Selector

The `selector` defines which Pods are managed by the Deployment. In the DSL, this block is required. At least one `matchLabels` entry must be set.

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

`matchLabels` describes simple key-value conditions. A Pod only matches if all specified labels exist and their values match.

```kotlin
selector {
    matchLabels {
        label("app", "demo")
        label("component", "api")
    }
}
```

## Match Expressions

`matchExpressions` allows more complex conditions with operators.

| Operator | Meaning |
| :--- | :--- |
| `In` | The label value must be included in the specified list of values. |
| `NotIn` | The label value must not be included in the specified list of values. |
| `Exists` | The label must exist. |
| `DoesNotExist` | The label must not exist. |

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

## Relationship to the Pod Template

The labels in `template.metadata` must satisfy the selector.

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

If the selector and template do not match, Kubernetes cannot manage the Deployment correctly.
