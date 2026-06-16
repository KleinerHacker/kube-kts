# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/).

This file lists only changes that affect end users — that is, the feature set of
the KTS scripts (the DSL for Kubernetes resources) and the `kube-kts` CLI tool.

## [Unreleased]

### Added

#### KTS DSL

- Define **chart metadata** via `chart(name, version) { }` (`ChartSpec`).
- Define **Deployment** resources via `deployment { }` (`DeploymentSpec`).
- Define **Service** resources via `service { }` (`ServiceSpec`), including
  advanced configuration options.
- Define **Ingress** resources via `ingress { }` (`IngressSpec`).
- **ConfigMap** support, including `binaryData` handling.
- **Values access** in scripts, relative to the `values:` root:
  `value`, `valueOrNull`, `array`, `map`, `exists`, as well as nested access.
- **Helper functions** via `*.lib.kts` files, available in all `*.spec.kts` files.
- **Legacy support**: existing `.yaml`/`.yml` files are copied unchanged
  (backward compatibility with classic Helm charts).
- **Script security model**: imports in KTS are disabled by default and must be
  explicitly allowed via the `--unsafe` flag.

#### CLI (`kube-kts`)

- `validate <repo>` — validate a repository.
- `compile <repo>` — compile KTS scripts.
- `render <repo> <target>` — render to Helm YAML.
- `lint <repo> <target>` — lint via Helm.
- `template <repo> <target> -n <name>` — run `helm template`.
- Flags `--unsafe` (allow imports in KTS) and `--experimental`
  (experimental YAML merge algorithms).
- Logging output for CLI runs.

#### Documentation

- Online documentation (MkDocs + Material) with localization (English / 简体中文)
  and dark mode.
