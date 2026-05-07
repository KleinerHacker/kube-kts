/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

@NoArgs
data class ContainerSpec(
    val name: String,
    val image: String,
    val imagePullPolicy: ImagePullPolicy?,
    val ports: List<PortSpec>?,
    val env: SingleEnvironmentSpec?,
    val envFrom: CompleteEnvironmentSpec?,
    val resources: Any?, //TODO
    val volumeMounts: List<VolumeMountSpec>?,
    val volumeDevices: List<VolumeDeviceSpec>?, //TODO
    val livenessProbe: Any?, //TODO
    val readinessProbe: Any?, //TODO
    val startupProbe: Any?, //TODO
    val lifecycle: Any?, //TODO
    val terminationMessagePath: String?,
    val terminationMessagePolicy: TerminationMessagePolicy?,
    val stdin: Boolean?,
    val stdinOnce: Boolean?,
    val tty: Boolean?,
    val securityContext: Any?, //TODO
    val command: List<String>?,
    val args: List<String>?,
    val workingDir: String?,
) {
    @Suppress("unused")
    enum class ImagePullPolicy {
        Always,
        IfNotPresent,
        Never
    }

    @Suppress("unused")
    enum class TerminationMessagePolicy {
        File,
        FallbackToLogsOnError
    }

    @NoArgs
    data class PortSpec(
        val name: String?,
        val containerPort: Int,
        val protocol: Protocol?
    )
}