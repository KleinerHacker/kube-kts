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

package org.pcsoft.framework.kube.kts.api.intern

/**
 * Marks a class as requiring a no-arguments constructor.
 *
 * This annotation is used internally to signal that the annotated class must include
 * a no-args constructor. It is commonly employed in scenarios like frameworks or tools
 * that rely on reflective instantiation, data serialization/deserialization, or custom
 * framework logic requiring instantiation without parameters.
 *
 * Note: This annotation is for internal use only and is not intended to be used
 * in public-facing APIs.
 */
internal annotation class NoArgs