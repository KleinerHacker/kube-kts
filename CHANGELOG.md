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
- `template <repo> <target> --name <name>` — run `helm template`.
- `install <repo> <target> --name <name>` — run `helm install`.
- `upgrade <repo> <target> --name <name>` — run `helm upgrade`
  (incl. `-i`/`--install`, `--reuse-values`/`--reset-values`/
  `--reset-then-reuse-values`, `--cleanup-on-fail`, `--history-max`,
  `--take-ownership`).
- `uninstall <repo> <target> --name <release>` — run `helm uninstall`
  (repeatable `--name` for multiple releases).
- **Full Helm flag forwarding**: every Helm-backed command (`lint`, `template`,
  `install`, `uninstall`) now forwards all supported Helm flags, including the
  global Helm flags (`--namespace`, `--kube-context`, `--kubeconfig`, `--kube-*`,
  `--burst-limit`, `--qps`, `--registry-config`, `--repository-*`), the value
  flags (`--set`, `--set-string`, `--set-file`, `--set-json`, `--set-literal`),
  chart-source/verification flags and rendering flags. See
  [HELM_SUPPORT.md](HELM_SUPPORT.md) for the coverage matrix.
- **Help marker column**: the `--help` output marks each option as forwarded to
  Helm (`---->`), experimental (`*`) or dangerous/security-relevant (`!!!`).
- `--debug` is now also forwarded to Helm.
- Flags `--unsafe` (allow imports in KTS) and `--experimental`
  (experimental YAML merge algorithms).
- Logging output for CLI runs.

### Changed

#### CLI (`kube-kts`)

- The release name for `template` is now passed via `--name` instead of `-n`.
  The `-n` shorthand is reserved for `--namespace`, to stay in sync with Helm.

#### Documentation

- Online documentation (MkDocs + Material) with localization
  (English / 简体中文 / 日本語 / 한국어) and dark mode.
- Dedicated CLI reference: an overview page plus one detailed page per command
  (`validate`, `compile`, `render`, `lint`, `template`, `install`, `uninstall`)
  documenting every flag, available in all four languages.
