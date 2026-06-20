# Job DSL

The `job` DSL is used to configure Kubernetes **Job** resources. A Job creates one or more Pods and
runs them to completion — suitable for batch and run-to-completion workloads, as opposed to the
long-running services managed by a [Deployment](deployment.md).

!!! warning "Security: Import Restrictions"
    By default, KTS scripts **do not allow** `import` statements or fully qualified class names
    (e.g. `java.lang.Runtime`). Only types provided via the pre-configured default imports may
    be used.

    Use the `--unsafe` flag to lift these restrictions.

## Basic Usage

A minimal Job requires `metadata` and a Pod `template` in `spec`. The Pod's `restartPolicy` must be
`Never` or `OnFailure`.

```kotlin
job {
    metadata("my-job") {
        namespace = "default"
    }

    spec {
        template {
            spec {
                containers {
                    container("worker", "busybox") { }
                }
                restartPolicy = PodSpec.RestartPolicy.Never
            }
        }
    }
}
```

## Detailed Example

The following is a comprehensive example showing parallelism, completions, retry handling,
automatic cleanup, an explicit selector, a Pod failure policy and a success policy.

```kotlin
job {
    metadata("full-job") {
        namespace = "batch"
    }

    spec {
        parallelism = 2
        completions = 5
        completionMode = JobSpec.CompletionMode.Indexed
        backoffLimit = 4
        backoffLimitPerIndex = 2
        maxFailedIndexes = 3
        activeDeadlineSeconds = 600.seconds.toJavaDuration()
        ttlSecondsAfterFinished = 3600.seconds.toJavaDuration()
        suspend = false
        manualSelector = true
        podReplacementPolicy = JobSpec.PodReplacementPolicy.Failed

        // Reuses the shared label selector DSL (same as Deployment)
        selector {
            matchLabels {
                label("app", "demo")
            }
        }

        // React to specific Pod failures
        podFailurePolicy {
            rule(PodFailurePolicySpec.Action.FailJob) {
                onExitCodes(PodFailurePolicySpec.OnExitCodes.Operator.In) {
                    containerName = "worker"
                    values(1, 42)
                }
            }
            rule(PodFailurePolicySpec.Action.Ignore) {
                onPodCondition("DisruptionTarget", "True")
            }
        }

        // Declare an indexed Job successful early
        successPolicy {
            rule {
                succeededIndexes = "0-2"
                succeededCount = 2
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
                    container("worker", "busybox") { }
                }
                restartPolicy = PodSpec.RestartPolicy.OnFailure
            }
        }
    }
}
```

## Configuration Reference

### Metadata (`metadata`)

| Property | Type | Description |
| :--- | :--- | :--- |
| `name` | `String` | The name of the Job resource (passed as the first argument). |
| `namespace` | `String?` | The namespace for the resource. |
| `generateName` | `String?` | An optional prefix for generating a unique name. |

### Job Specification (`spec`)

| Property / Method | Description |
| :--- | :--- |
| `parallelism` | The maximum number of Pods that should run in parallel. |
| `completions` | The desired number of successfully finished Pods. |
| `completionMode` | `NonIndexed` or `Indexed`. |
| `backoffLimit` | The number of retries before the Job is marked as failed. |
| `backoffLimitPerIndex` | The number of retries per index (indexed Jobs only). |
| `maxFailedIndexes` | The maximal number of failed indexes before the Job fails (indexed Jobs only). |
| `activeDeadlineSeconds` | The maximum duration the Job may be active before being terminated. |
| `ttlSecondsAfterFinished` | TTL after the Job finished, after which it is eligible for cleanup. |
| `suspend` | If true, the Job controller does not create any Pods. |
| `manualSelector` | If true, the `selector` is managed by the user instead of the system. |
| `podReplacementPolicy` | `TerminatingOrFailed` or `Failed`. |
| `selector { ... }` | The label selector (reuses the shared selector DSL, see [Selector](deployment/selector.md)). |
| `podFailurePolicy { rule(action) { ... } }` | Rules controlling how the Job reacts to Pod failures. |
| `successPolicy { rule { ... } }` | Rules defining when an indexed Job is declared successful. |
| `template { ... }` | The Pod template (see [Pod Template](deployment/template.md)). |

### Pod Failure Policy (`podFailurePolicy`)

| Property / Method | Description |
| :--- | :--- |
| `rule(action) { ... }` | Adds a rule. `action` is `FailJob`, `Ignore`, `Count` or `FailIndex`. |
| `onExitCodes(operator) { containerName; values(...) }` | Matches container exit codes (`operator`: `In`/`NotIn`). |
| `onPodCondition(type, status)` | Matches a Pod condition (e.g. `"DisruptionTarget", "True"`). |

### Success Policy (`successPolicy`)

| Property / Method | Description |
| :--- | :--- |
| `rule { ... }` | Adds a rule. |
| `succeededIndexes` | A set of indexes (e.g. `"0-2"`) that must succeed. |
| `succeededCount` | The minimum number of succeeded indexes. |

!!! note "restartPolicy"
    Unlike a Deployment, a Job's Pod template **must** set `restartPolicy` to `Never` or `OnFailure`.
    `Always` is not allowed for Jobs.
