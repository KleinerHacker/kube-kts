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

statefulSet {
    metadata("metadata") {
        namespace = "namespace"
        generateName = "generateName"
    }

    spec {
        replicas = 3
        serviceName = "my-service"
        podManagementPolicy = StatefulSetSpec.PodManagementPolicy.Parallel
        revisionHistoryLimit = 5

        selector {
            matchLabels {
                label("app", "demo")
            }
        }

        updateStrategy {
            type = StatefulSetUpdateStrategySpec.Type.RollingUpdate
            rollingUpdate {
                partition = 1
                maxUnavailable = 1.absolute
            }
        }

        volumeClaimTemplates {
            claim("data") {
                accessModes(VolumeClaimTemplateSpec.AccessMode.ReadWriteOnce)
                storageClassName = "standard"
                volumeMode = VolumeClaimTemplateSpec.VolumeMode.Filesystem
                requests {
                    storage = 1.giBytes
                }
            }
        }

        persistentVolumeClaimRetentionPolicy {
            whenDeleted = PersistentVolumeClaimRetentionPolicySpec.RetentionPolicyType.Delete
            whenScaled = PersistentVolumeClaimRetentionPolicySpec.RetentionPolicyType.Retain
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
            }
        }
    }
}
