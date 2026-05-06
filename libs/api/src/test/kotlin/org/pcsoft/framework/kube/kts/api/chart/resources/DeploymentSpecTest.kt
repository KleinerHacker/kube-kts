///*
// * Copyright (c) KleinerHacker alias pcsoft 2026.
// * This work is licensed under the Apache License, Version 2.0.
// * You may not use this file except in compliance with the License.
// * You may obtain a copy of the License at:
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and limitations.
// */
//
//package org.pcsoft.framework.kube.kts.api.chart.resources
//
//import org.junit.jupiter.api.Assertions
//import org.junit.jupiter.api.Test
//
//class DeploymentSpecTest {
//    @Test
//    fun testMaxContent() {
//        val spec = DeploymentSpecBuilder().apply {
//            replicas = 2
//            selector {
//                matchLabel("app", "demo")
//            }
//            template {
//                metadata("demo") {
//                    labels {
//                        label("app", "demo")
//                    }
//                }
//                spec {
//                    serviceAccountName = "demo-sa"
//                    nodeSelector("kubernetes.io/os", "linux")
//                    imagePullSecret("registry-secret")
//                    configMapVolume("config", "demo-config")
//                    container("app", "nginx:1.27") {
//                        imagePullPolicy = DeploymentSpec.ImagePullPolicy.IfNotPresent
//                        port(80, "http", DeploymentSpec.Protocol.TCP)
//                        env("ENV", "prod")
//                        volumeMount("config", "/etc/demo", true)
//                    }
//                }
//            }
//            strategy {
//                type = DeploymentSpec.DeploymentStrategyType.RollingUpdate
//                rollingUpdate {
//                    maxUnavailable = "25%"
//                    maxSurge = "1"
//                }
//            }
//        }.build()
//
//        Assertions.assertEquals(2, spec.replicas)
//        Assertions.assertEquals(mapOf("app" to "demo"), spec.selector.matchLabels)
//        Assertions.assertEquals("demo", spec.template.metadata?.name)
//        Assertions.assertEquals("demo-sa", spec.template.spec.serviceAccountName)
//        Assertions.assertEquals(1, spec.template.spec.containers.size)
//        Assertions.assertEquals("app", spec.template.spec.containers[0].name)
//        Assertions.assertEquals("nginx:1.27", spec.template.spec.containers[0].image)
//        Assertions.assertEquals(DeploymentSpec.Protocol.TCP, spec.template.spec.containers[0].ports?.first()?.protocol)
//        Assertions.assertEquals(DeploymentSpec.DeploymentStrategyType.RollingUpdate, spec.strategy?.type)
//        Assertions.assertEquals("25%", spec.strategy?.rollingUpdate?.maxUnavailable)
//    }
//}