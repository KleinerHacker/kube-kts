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

package org.pcsoft.framework.kube.kts.cli.commands.helm

/**
 * Contract for commands that assemble a complete `helm` command line.
 *
 * Implemented by the Helm command base classes (both the render-based and the render-less ones) so
 * that tests can obtain the forwarded argument list uniformly without knowing the concrete base
 * class. The leading `helm` executable itself is not part of the returned list.
 */
interface HelmCommandLineProvider {
    /**
     * Assembles the complete argument list that would be passed to the `helm` executable.
     *
     * @return the ordered list of Helm CLI arguments (without the leading `helm`).
     */
    fun buildHelmCommandLine(): List<String>
}
