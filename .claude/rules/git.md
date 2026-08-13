---
name: git and GitHub
---

# GIT

* All changes MUST be made through GIT:
    * Rename / move: `git mv`
    * Delete: `git rm`
    * Create: add with `git add` after creation
* Commits, pushes, pulls and any other actions communicating with the Git server MUST NEVER be invoked
    * Should this be required, the user MUST be asked
* Exceptions:
    * NEVER add plans or plan status files

## Target Environment

* GitHub MUST be used
* All files related to GitHub reside in `.github`
* For deeper structural changes, the pipeline MUST be checked and adjusted if necessary

### Pipeline

* There MUST be a pipeline for the regular build in `ci.yml`
    * It contains: [Build] Build -> Test, [Verify] Licences / Build and verify MkDocs
    * `Build` and `Verify` MUST run in parallel; everything within `Verify` MUST also run in parallel
* There MUST be a pipeline for a tag based release named `release.yml`
    * It contains: [Changelog] Verify against version -> ([Build] Build -> Test, [Verify] Verify licences, [MkDocs] Build -> Deploy, [Release] Push artifacts -> Write release)
    * `Changelog` runs first
    * `Build`, `Verify` and `MkDocs` run in parallel afterwards
        * `Verify` internally runs in parallel as well
    * `Release` runs at the end
        * Deployment errors MUST be ignored, but MUST be shown as a warning
