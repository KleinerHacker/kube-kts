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