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

package org.pcsoft.framework.kube.kts.definition.compiler

import org.pcsoft.framework.kube.kts.api.values.ValueAccess
import tools.jackson.dataformat.yaml.YAMLMapper
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.implicitReceivers
import kotlin.script.experimental.api.scriptsInstancesSharing

/**
 * Configuration class for evaluating KubeKTS scripts with specific settings.
 *
 * This class is designed to configure script evaluation by enabling instance sharing and
 * setting up implicit receivers for handling values using a `ValueAccess` instance.
 *
 * @param valueAccess The `ValueAccess` instance used to provide contextual data and support
 *                     for the script evaluation process.
 */
class KubeKtsEvaluationConfiguration(private val valueAccess: ValueAccess) : ScriptEvaluationConfiguration({
    scriptsInstancesSharing(true)
    implicitReceivers(valueAccess)
}) {
    @Suppress("unused")
    constructor() : this(ValueAccess.ofRoot(YAMLMapper().createObjectNode()))
}
