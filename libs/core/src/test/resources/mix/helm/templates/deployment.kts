#!/usr/bin/env kotlin

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

deployment {
    metadata("metadata") {
        namespace = "namespace"
        generateName = "generateName"
    }

    spec {
        replicas = 3

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

        template {
            metadata {
                labels {
                    label("app", "demo")
                }
                annotations {
                    annotation("description", "Demo pod template")
                }
            }

            spec {
                containers {
                    container("name", "image") {

                    }
                }

                restartPolicy = PodSpec.RestartPolicy.Always
            }
        }

        strategy {
            type = DeploymentStrategySpec.Type.RollingUpdate
            rollingUpdate {
                maxSurge = 10.percent
                maxUnavailable = 3.absolute
            }
        }

        minReadySeconds = 30.seconds.toJavaDuration()
        revisionHistoryLimit = 5
        paused = true
        progressDeadlineSeconds = 600.seconds.toJavaDuration()
    }
}