/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

job {
    metadata("metadata") {
        namespace = "namespace"
        generateName = "generateName"
    }

    spec {
        parallelism = 2
        completions = 5
        completionMode = JobSpec.CompletionMode.Indexed
        backoffLimit = 4
        backoffLimitPerIndex = 2
        maxFailedIndexes = 3
        suspend = false
        manualSelector = true
        podReplacementPolicy = JobSpec.PodReplacementPolicy.Failed

        selector {
            matchLabels {
                label("app", "demo")
            }
        }

        podFailurePolicy {
            rule(PodFailurePolicySpec.Action.FailJob) {
                onExitCodes(PodFailurePolicySpec.OnExitCodes.Operator.In) {
                    containerName = "name"
                    values(1, 42)
                }
            }
        }

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
                    container("name", "image") {

                    }
                }

                restartPolicy = PodSpec.RestartPolicy.Never
            }
        }
    }
}
