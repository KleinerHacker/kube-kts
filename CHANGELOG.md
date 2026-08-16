# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres
to [Semantic Versioning](https://semver.org/).

This file lists only changes that affect end users — that is, the feature set of the KTS scripts (the DSL for Kubernetes
resources) and the `kube-kts` CLI tool.

## [Unreleased]

## [0.1.0]

### Added

#### KTS DSL

- Define **chart metadata** via `chart(name, version) { }` (`ChartSpec`).
- Define **Deployment** resources via `deployment { }` (`DeploymentSpec`).
- Define **Service** resources via `service { }` (`ServiceSpec`), including advanced configuration options.
- Define **Ingress** resources via `ingress { }` (`IngressSpec`).
- **ConfigMap** support, including `binaryData` handling.
- **Values access** in scripts, relative to the `values:` root:
  `value`, `valueOrNull`, `array`, `map`, `exists`, as well as nested access.
- **Helper functions** via `*.lib.kts` files, available in all `*.spec.kts` files.
- **Legacy support**: existing `.yaml`/`.yml` files are copied unchanged (backward compatibility with classic Helm
  charts).
- **Script security model**: imports in KTS are disabled by default and must be explicitly allowed via the `--unsafe`
  flag.
- **StatefulSet** resources via `statefulSet { }` (`StatefulSetSpec`), **Job**
  resources via `job { }` (`JobSpec`), **Secret**, **SealedSecret** and OpenShift **Route** resources.
- **Complete volume source coverage**: in addition to `configMap`, `secret`,
  `persistentVolumeClaim`, `hostPath` and `emptyDir`, all remaining Kubernetes sources are now available — `projected`,
  `downwardApi`, `csi`, `ephemeral`,
  `image`, `nfs`, `iscsi`, `fibreChannel`, `rbd`, `cephFs`, `glusterFs`,
  `awsElasticBlockStore`, `gcePersistentDisk`, `azureDisk`, `azureFile`,
  `cinder`, `portworx` and `vsphereVolume`. Sources Kubernetes has removed (`gitRepo`, `flexVolume`, `flocker`,
  `quobyte`, `scaleIo`, `storageOs`,
  `photonPersistentDisk`) are available but marked deprecated.
- **Named ports**: every port field that Kubernetes models as `IntOrString`
  now accepts a container port name as well as a number — `httpGet("http")`,
  `tcpSocket("http")` and `targetPortName` on a service port.
- **Volume mounts** now support `subPath`, `subPathExpr`, `mountPropagation`
  and `recursiveReadOnly`.
- **Native sidecars**: an init container can set `restartPolicy = Always`.
- **In-place resize**: containers can declare `addResizePolicy(...)` per resource.
- **Dynamic Resource Allocation**: containers reference pod resource claims via
  `resources { addClaim("gpu") }`.
- **Container ports** now support `hostPort` and `hostIP`.
- **Pod-level settings** added: `schedulingGates`, `hostUsers`, pod-level
  `resources`, and `seLinuxChangePolicy` in the security context.
- **Volume claim templates** now support `selector`, `volumeName`, `dataSource`,
  `dataSourceRef` and `volumeAttributesClassName`.
- **Job** gained `managedBy`, **Route** gained `subdomain` and `httpHeaders`.

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
  `install`, `uninstall`) now forwards all supported Helm flags, including the global Helm flags (`--namespace`,
  `--kube-context`, `--kubeconfig`, `--kube-*`,
  `--burst-limit`, `--qps`, `--registry-config`, `--repository-*`), the value flags (`--set`, `--set-string`,
  `--set-file`, `--set-json`, `--set-literal`), chart-source/verification flags and rendering flags.
- **Help marker column**: the `--help` output marks each option as forwarded to Helm (`---->`), experimental (`*`) or
  dangerous/security-relevant (`!!!`).
- `--debug` is now also forwarded to Helm.
- Flags `--unsafe` (allow imports in KTS) and `--experimental`
  (experimental YAML merge algorithms).
- Logging output for CLI runs.

### Changed

#### KTS DSL

- **Breaking:** `env { }` and `envFrom { }` on a container now produce YAML lists instead of single objects, so a
  container can define more than one environment variable. Existing scripts keep working: `env(name) { }` can now simply
  be called several times, and the new `envs { }` / `envsFrom { }` blocks group multiple entries.
- **Breaking:** `home` in `chart { }` is now a `URI` instead of a `String`, consistent with `icon` and `sources`.
- **Breaking:** a Route must now declare its primary backend via `to(...)`, and its `kind` is an enum instead of a
  free-form string.
- **Breaking:** a service port name is now optional; `ports` themselves are only required for service types other than
  `ExternalName`.
- `ephemeralContainers` and `clusterName` are deprecated and are no longer written to the rendered manifest — Kubernetes
  does not accept either through a manifest.
- Every specification type now has its own builder, so all nested structures can be configured through a block instead
  of positional parameters. New blocks are available for downward API items (`addItem(path) { fieldRef(...) }`),
  projected volume sources (`addSource { configMap { } }`), container resize policies (`resizePolicies { }`), container
  resource claims (`claim(name) { }`), route HTTP header actions (`actions { }`), StatefulSet ordinals (`ordinals { }`)
  and the claim template of an ephemeral volume (`volumeClaimTemplate { }`). The previous shorthand functions keep
  working.

#### CLI (`kube-kts`)

- The release name for `template` is now passed via `--name` instead of `-n`. The `-n` shorthand is reserved for
  `--namespace`, to stay in sync with Helm.

#### Documentation

- Online documentation (MkDocs + Material) with localization (English / 简体中文 / 日本語 / 한국어) and dark mode.
- Dedicated CLI reference: an overview page plus one detailed page per command (`validate`, `compile`, `render`, `lint`,
  `template`, `install`, `uninstall`)
  documenting every flag, available in all four languages.

### Fixed

#### KTS DSL

- `imagePullSecrets` and `readinessGates` were rendered as plain string lists instead of the object lists Kubernetes
  expects, producing manifests the API server rejected.
- `dnsConfig.options` and the `httpHeaders` of probes and lifecycle hooks were rendered as objects instead of `name`/
  `value` lists.
- Memory and CPU values could be written but not read back, so a rendered chart could not be parsed into a specification
  again.
