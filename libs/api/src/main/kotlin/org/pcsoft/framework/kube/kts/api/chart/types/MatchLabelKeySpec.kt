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

package org.pcsoft.framework.kube.kts.api.chart.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Represents a specification for matching label keys.
 *
 * This data class contains a list of string keys that can be used for
 * matching purposes in various contexts. The keys may be nullable, and
 * their usage is dependent on the specific implementation logic that
 * interacts with this specification.
 *
 * @property keys A list of string keys to be matched, or null if no keys are specified.
 */
@NoArgs
data class MatchLabelKeySpec(
    val keys: List<String>?
)