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

ingress {
    metadata("metadata") {
        namespace = "namespace"
        generateName = "generateName"
    }

    spec {
        ingressClassName = "className"

        defaultServiceBackend("service") {
            port(9999)
        }

        if (exists("security.tls")) {
            tlsList {
                tls {
                    secretName = value<String>("security.tls.secret")
                    hosts {
                        host(value<String>("security.tls.host"))
                    }
                }
            }
        }

        rules {
            rule {
                host = "rule.example.com"
                httpPaths {
                    httpPath(RulesSpec.HttpPathConfig.PathType.Exact) {
                        path = "path"
                        serviceBackend("ruleService") {
                            port(7777)
                        }
                    }
                }
            }
        }
    }
}