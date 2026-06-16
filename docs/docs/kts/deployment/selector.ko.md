# Deployment Selector

`selector`는 Deployment가 어떤 Pod를 관리할지 정의합니다. DSL에서 이 블록은 필수입니다. 최소 하나의 `matchLabels` 항목을 설정해야 합니다.

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

`matchLabels`는 단순한 키-값 조건을 기술합니다. 지정된 모든 레이블이 존재하고 그 값이 일치할 때만 Pod가 일치합니다.

```kotlin
selector {
    matchLabels {
        label("app", "demo")
        label("component", "api")
    }
}
```

## Match Expressions

`matchExpressions`는 연산자를 사용한 더 복잡한 조건을 지정할 수 있게 합니다.

| 연산자 | 의미 |
| :--- | :--- |
| `In` | 레이블 값이 지정된 값 목록에 포함되어야 합니다. |
| `NotIn` | 레이블 값이 지정된 값 목록에 포함되어서는 안 됩니다. |
| `Exists` | 레이블이 존재해야 합니다. |
| `DoesNotExist` | 레이블이 존재해서는 안 됩니다. |

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

## Pod 템플릿과의 관계

`template.metadata`의 레이블은 selector를 만족해야 합니다.

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

selector와 template이 일치하지 않으면 Kubernetes는 Deployment를 올바르게 관리할 수 없습니다.
