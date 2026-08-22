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
    * The required pipeline structure is defined in the skill `github-pipeline`
