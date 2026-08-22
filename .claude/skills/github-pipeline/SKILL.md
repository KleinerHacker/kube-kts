---
name: github-pipeline
description: Required structure of the GitHub Actions pipelines (ci.yml, release.yml) - load when workflows are created, changed or verified, or after structural changes to the project
---

# Pipeline

* There MUST be a pipeline for the regular build in `ci.yml`
    * It contains: [Build] Build -> Test, [Verify] Licences / Build and verify MkDocs
    * `Build` and `Verify` MUST run in parallel; everything within `Verify` MUST also run in parallel
* There MUST be a pipeline for a tag based release named `release.yml`
    * It contains: [Changelog] Verify against version -> ([Build] Build -> Test, [Verify] Verify licences, [MkDocs]
      Build -> Deploy, [Release] Push artifacts -> Write release)
    * `Changelog` runs first
    * `Build`, `Verify` and `MkDocs` run in parallel afterwards
        * `Verify` internally runs in parallel as well
    * `Release` runs at the end
        * Deployment errors MUST be ignored, but MUST be shown as a warning
