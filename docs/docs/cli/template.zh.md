# template

```bash
kube-kts template <REPOSITORY> [TARGET] --name <NAME> [选项]
```

将仓库渲染为 Helm Chart，然后运行 `helm template` 将完全渲染的 Kubernetes 清单打印到标准输出。与
[`install`](install.md) 不同，它**不会**连接或改变集群（除非你用 `--validate` 主动选择），因此是查看
将被应用内容的最安全方式。它非常适合对变更做差异比较、将输出交给 `kubectl apply`/GitOps，或调试值如何
影响结果。

## 工作原理

1. 扫描、编译并将仓库渲染到（临时或显式的）Chart 目录，并应用你的 `-f`/`--set*` 值。
2. 在该目录中执行 `helm template <NAME> .`，并附加所有被转发的选项。
3. 渲染的清单写入标准输出（或用 `--output-dir` 写入文件）。

!!! note "发布名称是选项，命名空间是 `-n`"
    发布名称用 `--name` 传入，并作为位置参数 `NAME` 转发给 Helm。`-n` 短选项保留给 `--namespace`，
    与 Helm 一致。若不关心名称可使用 `--generate-name`。

!!! tip "本地渲染与集群感知渲染"
    默认情况下 template 完全离线。添加 `--validate` 可针对实时集群校验清单，或用 `-a`/`--kube-version`
    固定渲染期间使用的 API 能力。

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `REPOSITORY` | 是 | 要 template 的仓库路径。若省略，则使用当前工作目录。 |
| `TARGET` | 否 | template 前 Chart 渲染到的目录。若省略，则使用临时目录。 |

## template 选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--name=NAME` | | 渲染时使用的发布名称。作为位置参数 `NAME` 转发给 Helm。仅在配合 `--generate-name` 时省略。 |
| `-a`, `--api-versions=VERSIONS` | `---->` | 通过 `Capabilities.APIVersions` 报告为可用的 API 版本，如 `networking.k8s.io/v1`。可在不查询集群的情况下渲染为某些 API 存在。可重复。 |
| `--include-crds` | `---->` | 在输出中包含 Chart 的 CRD。`helm template` 默认会省略它们。 |
| `--is-upgrade` | `---->` | 通过设置 `.Release.IsUpgrade`（并清除 `.Release.IsInstall`）按升级方式渲染，从而触发仅升级的模板分支。 |
| `--kube-version=VERSION` | `---->` | 通过 `Capabilities.KubeVersion` 报告的 Kubernetes 版本，如 `1.29`。便于离线为特定集群版本渲染。 |
| `--output-dir=DIR` | `---->` | 将每个渲染的模板写入该目录下的单独文件，而非全部打印到标准输出。 |
| `-s`, `--show-only=TEMPLATE` | `---->` | 仅输出由给定模板路径产生的清单，如 `templates/deployment.yaml`。可重复。 |
| `--skip-tests` | `---->` | 从输出中省略测试清单（标注为 Helm 测试的资源）。 |
| `--validate` | `---->` | 针对当前所指向的集群校验渲染的清单。需要集群访问权限，使 template 成为集群感知操作。 |
| `--dry-run` | `---->` | 渲染期间模拟安装，影响检查 dry-run 状态的模板分支。 |
| `-g`, `--generate-name` | `---->` | 让 Helm 生成发布名称；用于替代 `--name`。 |
| `-l`, `--labels=KEY=VALUE` | `---->` | 向（模拟的）发布元数据添加标签。可重复。 |
| `--create-namespace` | `---->` | 在渲染输出中标记命名空间为待创建，与安装时的标志一致。 |

## 值选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--set=KEY=VALUE` | `---->` | 内联覆盖值，如 `--set replicas=3`。类型会被推断。可重复；后面的 `--set` 胜出并覆盖 `-f` 文件。 |
| `--set-string=KEY=VALUE` | `---->` | 与 `--set` 类似，但值始终保持为字符串。可重复。 |
| `--set-file=KEY=PATH` | `---->` | 将 `PATH` 处文件的全部内容作为 `KEY` 的值。可重复。 |
| `--set-json=KEY=JSON` | `---->` | 从 JSON 表达式设置值，可表示对象/数组。可重复。 |
| `--set-literal=KEY=VALUE` | `---->` | 完全按所写设置值，不做类型转换。可重复。 |
| `-f`, `--values=FILE` | `---->` | 用于渲染并转发给 Helm 的 YAML 值文件。可重复且按顺序叠加；`--set*` 会覆盖它们。 |

## Chart 来源与校验选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--repo=URL` | `---->` | 从该仓库 URL 拉取 Chart，而非本地路径。 |
| `--username=USER` | `---->` | Chart 仓库用户名（与 `--password` 配合）。 |
| `--password=PASSWORD` | `---->` | Chart 仓库密码。建议使用密钥/环境变量，而非命令行。 |
| `--pass-credentials` | `---->` | 将仓库凭据发送到所有域，而不仅是 Chart 的主机。仅限受信任仓库。 |
| `--ca-file=FILE` | `---->` | 用于验证 HTTPS Chart 服务器 TLS 证书的 CA 包。 |
| `--cert-file=FILE` | `---->` | 用于对 Chart 服务器进行双向 TLS 的客户端 TLS 证书。 |
| `--key-file=FILE` | `---->` | 与 `--cert-file` 匹配的客户端 TLS 私钥。 |
| `--insecure-skip-tls-verify` | `---->` | 下载 Chart 时跳过 TLS 验证。不安全——生产环境请避免。 |
| `--keyring=FILE` | `---->` | 用于验证签名 Chart 的公钥钥匙串（与 `--verify` 配合）。默认 `~/.gnupg/pubring.gpg`。 |
| `--verify` | `---->` | 使用前验证 Chart 的来源签名；缺失或无效时失败。 |
| `--version=VERSION` | `---->` | 选择 Chart 版本的 SemVer 约束。默认使用最新稳定版本。 |
| `--devel` | `---->` | 也考虑预发布版本（等同于 `>0.0.0-0`）。 |
| `--dependency-update` | `---->` | template 前下载/更新缺失的 Chart 依赖。 |

## 渲染选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--no-hooks` | `---->` | 渲染期间跳过所有 Chart 生命周期 hooks。 |
| `--disable-openapi-validation` | `---->` | 不根据 OpenAPI 模式校验渲染的清单。 |
| `--name-template=TEMPLATE` | `---->` | 用于计算发布名称的 Go 模板，作为固定 `--name` 的替代。 |
| `--render-subchart-notes` | `---->` | 同时渲染子 Chart 的 NOTES.txt。 |
| `--skip-crds` | `---->` | 不渲染 Chart 附带的 CRD（与 `--include-crds` 相反）。 |
| `--post-renderer=PATH` | `---->` | 对渲染清单运行的可执行文件，可实现 Kustomize 式后处理。 |
| `--post-renderer-args=ARG` | `---->` | 传给 `--post-renderer` 可执行文件的参数。可重复。 |
| `--timeout=DURATION` | `---->` | 等待任一单个 Kubernetes 操作的最长时间，Go 时长格式（默认 `5m0s`）。主要与 `--validate` 相关。 |

## Helm 全局选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | 渲染时假定的命名空间（影响命名空间级资源和 `.Release.Namespace`）。默认为当前 kube-context 的命名空间。 |
| `--kube-context=CONTEXT` | `---->` | 要使用的 kubeconfig 上下文（与 `--validate` 相关）。 |
| `--kubeconfig=FILE` | `---->` | 要使用的 kubeconfig 文件路径，替代默认值。 |
| `--kube-apiserver=ADDRESS` | `---->` | 覆盖 kubeconfig 中的 API 服务器地址和端口。 |
| `--kube-as-user=USER` | `---->` | 模拟该用户；需要模拟权限。 |
| `--kube-as-group=GROUP` | `---->` | 模拟该用户组。可重复。 |
| `--kube-ca-file=FILE` | `---->` | 用于验证 API 服务器 TLS 证书的 CA 证书文件。 |
| `--kube-token=TOKEN` | `---->` | 用于认证的 Bearer 令牌，替代 kubeconfig 凭据。 |
| `--kube-tls-server-name=NAME` | `---->` | 验证 API 服务器证书时使用的服务器名称。 |
| `--kube-insecure-skip-tls-verify` | `---->` | 跳过 API 服务器证书验证。不安全——仅限受信任/测试。 |
| `--burst-limit=INT` | `---->` | 对 API 请求的客户端节流突发上限（默认 `100`）。 |
| `--qps=QPS` | `---->` | 与 API 服务器通信的客户端每秒查询数上限。 |
| `--registry-config=FILE` | `---->` | OCI 注册表配置（凭据）文件路径。 |
| `--repository-cache=DIR` | `---->` | 用于解析依赖的缓存仓库索引目录。 |
| `--repository-config=FILE` | `---->` | 将仓库名称映射到 URL 的文件路径（`repositories.yaml`）。 |

## 全局选项

同样支持所有[全局选项](index.md)（如 `--debug`、`--unsafe`）。

## 示例

```bash
# 打印发布 "my-app" 的渲染清单
kube-kts template /path/to/repository --name my-app

# 仅单个模板，包含 CRD，并带内联覆盖
kube-kts template . --name my-app -s templates/deployment.yaml --include-crds --set replicas=3

# 为特定集群版本渲染并写入目录
kube-kts template ./helm --name my-app --kube-version 1.29 --output-dir ./manifests
```
