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

package org.pcsoft.framework.kube.kts.logging

/**
 * Unicode checkmark symbol (✓) used to indicate successful operations or status.
 */
const val symbolSuccess = "\u2713"

/**
 * Unicode cross mark symbol (✗) used to indicate failed operations or status.
 */
const val symbolFailed = "\u2717"

/**
 * Unicode warning symbol (⚠) used to indicate warnings or cautionary messages.
 */
const val symbolWarning = "\u26A0"

/**
 * Unicode right arrow symbol (→) used to indicate direction or progression in logs and messages.
 */
const val symbolArrowRight = "\u2192"

/**
 * Represents a bullet point symbol (Unicode U+2022) used for itemization in log messages.
 *
 * This constant is used to visually indicate list items or sub-processes in debug and trace logs,
 * providing better readability when displaying hierarchical or multi-step operations.
 */
const val symbolBullet = "\u2022"

/**
 * Unicode symbol representing a gear or cogwheel (⟫).
 *
 * This symbol is used in logging contexts to visually indicate ongoing processes,
 * particularly during rendering operations where configuration or transformation tasks are being performed.
 */
const val symbolProcess = "\u27F3"

/**
 * Unicode symbol representing a sub-process indicator (⊻).
 *
 * This symbol is used in logging contexts to visually denote operations related to sub-processes or nested tasks,
 * particularly when merging YAML files where multiple overlays are applied sequentially.
 */
const val symbolSubProcess = "\u21BB"

/**
 * Unicode symbol representing a main process or task.
 *
 * This symbol is used in logging output to visually indicate the start of a primary operation or process.
 */
const val symbolMainProcess = "\u25CC"