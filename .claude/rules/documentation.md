---
name: documentation
---

# Documentation

## Code

* EVERY public member in EVERY source file (except automatically generated source files) MUST be documented with KDoc
* EVERY test method MUST be documented with a detailed KDoc describing the use case

## Readme

* There MUST be instructions on how to check out, build and run the project
* EVERY feature MUST be documented in a bullet point
* EVERY supported command (CLI) MUST be documented in a bullet point
* EVERY supported template (Helm template) MUST be documented in a bullet point
* There MUST be instructions on how to consume the artifacts
* There MUST be a short outline of the "WHAT" of the project
* There MUST be a reference to the MkDocs documentation (gh-pages), the API documentation and the licence report
* The readme MUST be checked automatically after changes and adjusted if required

## MkDocs

* MkDocs MUST be integrated under `docs`
* Structure:
    * `docs/mkdocs.yml` - Root file
    * `docs/docs` - *.md files
    * `docs/docs/assets` - Further asset files (MUST reside inside `docs_dir` so that MkDocs ships them)
    * `docs/docs/stylesheets` - Additional CSS files
* The documentation MUST be checked after every change and adjusted if necessary

## CHANGELOG.md

* A changelog file MUST be present
* It MUST be updated with the applied changes after a change
    * Only changes that are visible to the user MUST go into the changelog
* The prescribed format MUST be kept
    * New entries MUST go under `[UNRELEASED]`

## External Documentation

* The Helm reference documentation is available here: `https://helm.sh/docs`
