---
name: development
---

# Development

## Planning

* A plan MUST be created for EVERY change; ALWAYS ask the user whether a plan should be created
    * A switch to plan mode MUST happen
* The PLAN MUST ALWAYS be written in GERMAN - both the plan file and the console output
    * This applies to headings, bullet points and every other text of the plan
* The PLAN MUST NOT contain a summary or an explanation of the changes
    * FORBIDDEN sections: "Context", "Background", "Summary", "Overview", "Rationale", "Trade-offs"
    * FORBIDDEN: prose paragraphs of any kind - the plan consists of bullet points ONLY
* The implementation tasks MUST be explained in short bullet points with no more than 20 words per bullet and a maximum of 10 bullets per task
    * A bullet describes WHAT is done, NOT WHY
* Before leaving plan mode, the plan MUST be checked against ALL rules above
* The plan MUST be written into the local `.claude/plans` directory, together with a status file
    * Naming scheme:
        * Plan: `<Name>.md`
        * Status: `<Name>-status.md`
    * The status MUST ALWAYS be kept up to date
* When restarting an existing plan after an interruption, plan mode MUST be entered
    * The remaining items are laid out again according to the prescribed scheme
* After a plan is finished, clean up the `.claude/plans` directory

## Implementation

* Kotlin MUST ALWAYS be used
* Gradle MUST ALWAYS be used

* All changes to a single file MUST be applied in ONE single tool call
    * Before editing, ALL required changes to that file MUST be collected and planned completely
    * The file is then written EXACTLY ONCE - with the `Write` tool (full content) or with a
      SINGLE `Edit` call
    * FORBIDDEN: several consecutive `Edit` calls on the same file for the same change
    * FORBIDDEN: incremental "edit -> read -> edit again" cycles on the same file
    * If a change to file A reveals a follow-up change in file A, the file MUST NOT be patched
      again - the complete new content MUST be written in one operation instead
    * This rule applies per file, NOT per task: several DIFFERENT files MAY be edited in
      parallel, each with exactly one call

## Building

* A build MUST always be performed with the Gradle target `build` after every change

## Testing

* Every use case MUST be tested
* Code coverage should reach at least 100%
* The package structure of the production code MUST be mirrored
* Tests MUST be split into two categories, distinguished by the class name suffix
    * **Developer tests** - Simple unit tests covering individual pieces of functionality
        * The class name MUST end with `Test` (e.g. `PortSpecTest`)
        * They MUST NOT depend on external tools (e.g. the `helm` binary) or on the network
        * They are executed by the Gradle task `developerTest`
    * **Integration tests** - Tests covering complete features or aiming at performance
        * The class name MUST end with `IT` (e.g. `InstallIT`)
        * They MAY start external processes and run the complete pipeline
        * They are executed by the Gradle task `integrationTest`
    * The Gradle task `test` MUST execute both categories

## Coverage

* Code coverage MUST be measured with Kover
* The aggregated report is created with the Gradle task `koverHtmlReport`
