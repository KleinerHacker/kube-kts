/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

sealedSecret {
    metadata("sealedsecret") {
        namespace = "namespace"
    }

    spec {
        encryptedData {
            entry("password", "AgBy3i4OJSWK+PiTySYZZA9rO")
        }
        template {
            type = SecretSpec.Type.Opaque
            immutable = true
            metadata {
                labels {
                    label("app", "demo")
                }
            }
        }
    }
}
