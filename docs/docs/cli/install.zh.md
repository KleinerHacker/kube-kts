# install

```bash
kube-kts install <REPOSITORY> [TARGET] --name <NAME> [选项]
```

将仓库渲染为 Helm Chart，然后运行 `helm install` 将其作为新发布部署到 Kubernetes 集群。这是真正改变
集群状态的命令，因此它会使用你的 kube-context（或下文的覆盖项）与 API 服务器通信，并转发所有受支持的
Helm install 标志。

## 工作原理

1. 扫描、编译并将仓库渲染到（临时或显式的）Chart 目录，并应用你的 `-f`/`--set*` 值。
2. 在该目录中执行 `helm install <NAME> .`，并附加所有被转发的选项。
3. Helm 创建发布；命令的退出码反映 Helm 的结果。失败时，除非传入 `--atomic`，否则不会回滚。

!!! note "发布名称是选项，命名空间是 `-n`"
    发布名称用 `--name` 传入，并作为位置参数 `NAME` 转发给 Helm。`-n` 短选项有意**不是**名称——它
    保留给 `--namespace`，以与 Helm 保持一致。仅在配合 `--generate-name` 时才可省略 `--name`。

!!! tip "更安全的发布"
    将 `--atomic`（失败时回滚）与 `--wait` / `--wait-for-jobs` 及合理的 `--timeout` 结合使用，
    使失败的安装不会留下半创建的资源。

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `REPOSITORY` | 是 | 要安装的仓库路径。若省略，则使用当前工作目录。 |
| `TARGET` | 否 | 安装前 Chart 渲染到的目录。若省略，则使用临时目录。 |

## install 选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--name=NAME` | | 要创建的发布名称。作为位置参数 `NAME` 转发给 Helm。除非使用 `--generate-name`，否则必填。 |
| `--atomic` | `---->` | 安装失败时自动删除已创建的一切，使集群恢复原状。隐含 `--wait`。强烈推荐用于无人值守/CI 安装。 |
| `--create-namespace` | `---->` | 若目标命名空间（来自 `-n`/`--namespace`）不存在则创建，而非失败。 |
| `--dry-run` | `---->` | 模拟安装并显示将发生的内容，但不改变集群。非常适合预览发布。 |
| `--enable-dns` | `---->` | 允许模板在渲染期间执行 DNS 查询（Helm 的 `getHostByName` 等）。为可重现性默认关闭。 |
| `--force` | `---->` | 通过替换强制更新资源，用于从卡住状态恢复。可能具有破坏性——可能会删除并重建资源。 |
| `-g`, `--generate-name` | `---->` | 让 Helm 自动生成发布名称；用于替代 `--name`。 |
| `-l`, `--labels=KEY=VALUE` | `---->` | 向发布元数据（Helm 发布对象，而非渲染出的资源）添加标签。可重复。 |
| `--description=TEXT` | `---->` | 为本次发布修订附加自定义的可读描述。 |
| `-o`, `--output=FORMAT` | `---->` | 命令结果输出格式：`table`（默认）、`json` 或 `yaml`。编写脚本时使用 `json`/`yaml`。 |
| `--replace` | `---->` | 复用历史中仍存在的、先前已删除发布的名称。不推荐用于生产；最好先干净卸载再安装。 |
| `--wait` | `---->` | 在标记发布成功前，阻塞直到所有创建的资源（Pod、PVC、Service、Deployment 等）报告就绪。遵循 `--timeout`。 |
| `--wait-for-jobs` | `---->` | 在 `--wait` 之外，还阻塞直到发布创建的所有 Job 完成。 |

## 值选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--set=KEY=VALUE` | `---->` | 内联覆盖值，如 `--set image.tag=1.2.3`。类型会被推断。可重复；后面的 `--set` 胜出并覆盖 `-f` 文件。 |
| `--set-string=KEY=VALUE` | `---->` | 与 `--set` 类似，但始终保持为字符串（如保留 `"01"`）。可重复。 |
| `--set-file=KEY=PATH` | `---->` | 将 `PATH` 处文件的全部内容作为 `KEY` 的值（证书、脚本等）。可重复。 |
| `--set-json=KEY=JSON` | `---->` | 从 JSON 表达式设置值，可表示对象/数组，如 `--set-json 'ports=[80,443]'`。可重复。 |
| `--set-literal=KEY=VALUE` | `---->` | 完全按所写设置值，不做任何类型转换。可重复。 |
| `-f`, `--values=FILE` | `---->` | 用于渲染并转发给 Helm 的 YAML 值文件。可重复且按顺序叠加；`--set*` 会覆盖它们。 |

## Chart 来源与校验选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--repo=URL` | `---->` | 从该仓库 URL 拉取 Chart，而非本地路径。 |
| `--username=USER` | `---->` | Chart 仓库用户名（与 `--password` 配合）。 |
| `--password=PASSWORD` | `---->` | Chart 仓库密码。建议使用密钥/环境变量，而非在命令行传递。 |
| `--pass-credentials` | `---->` | 将仓库凭据发送到所有域，而不仅是 Chart 的主机。仅对受信任的仓库使用。 |
| `--ca-file=FILE` | `---->` | 用于验证 HTTPS Chart 服务器 TLS 证书的 CA 包。 |
| `--cert-file=FILE` | `---->` | 用于对 Chart 服务器进行双向 TLS 的客户端 TLS 证书。 |
| `--key-file=FILE` | `---->` | 与 `--cert-file` 匹配的客户端 TLS 私钥。 |
| `--insecure-skip-tls-verify` | `---->` | 下载 Chart 时跳过 TLS 验证。不安全——生产环境请避免。 |
| `--keyring=FILE` | `---->` | 用于验证签名 Chart 的公钥钥匙串（与 `--verify` 配合）。默认 `~/.gnupg/pubring.gpg`。 |
| `--verify` | `---->` | 安装前验证 Chart 的来源签名；缺失或无效时失败。 |
| `--version=VERSION` | `---->` | 选择 Chart 版本的 SemVer 约束，如 `1.2.3` 或 `^1.2`。默认使用最新稳定版本。 |
| `--devel` | `---->` | 也考虑预发布版本（等同于 `>0.0.0-0`）。 |
| `--dependency-update` | `---->` | 安装前下载/更新缺失的 Chart 依赖（类似 `helm dependency update`）。 |

## 渲染选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--no-hooks` | `---->` | 安装期间跳过所有 Chart 生命周期 hooks（pre/post-install 等）。 |
| `--disable-openapi-validation` | `---->` | 不根据集群的 OpenAPI 模式校验渲染的清单。更快，但跳过一道安全保障。 |
| `--name-template=TEMPLATE` | `---->` | 用于计算发布名称的 Go 模板，作为固定 `--name` 的替代。 |
| `--render-subchart-notes` | `---->` | 同时渲染子 Chart 的 NOTES.txt，而不仅是父 Chart。 |
| `--skip-crds` | `---->` | 不安装 Chart 附带的 CRD（当 CRD 单独管理时使用）。 |
| `--post-renderer=PATH` | `---->` | 在清单被应用前对其运行的可执行文件，可实现 Kustomize 式后处理。 |
| `--post-renderer-args=ARG` | `---->` | 传给 `--post-renderer` 可执行文件的参数。可重复。 |
| `--timeout=DURATION` | `---->` | 等待任一单个 Kubernetes 操作的最长时间，Go 时长格式（`5m0s`、`90s` 等）。默认 `5m0s`。与 `--wait` 配合时最为相关。 |

## Helm 全局选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | 发布安装到的命名空间。若不存在，请配合 `--create-namespace`。默认为当前 kube-context 的命名空间。 |
| `--kube-context=CONTEXT` | `---->` | 要使用的 kubeconfig 上下文，可在不切换当前上下文的情况下指向特定集群/用户。 |
| `--kubeconfig=FILE` | `---->` | 要使用的 kubeconfig 文件路径，替代 `$KUBECONFIG` / `~/.kube/config`。 |
| `--kube-apiserver=ADDRESS` | `---->` | 覆盖 kubeconfig 中的 API 服务器地址和端口。 |
| `--kube-as-user=USER` | `---->` | 模拟该用户（RBAC 用户模拟）；需要模拟权限。 |
| `--kube-as-group=GROUP` | `---->` | 模拟该用户组。可重复以模拟多个组。 |
| `--kube-ca-file=FILE` | `---->` | 用于验证 API 服务器 TLS 证书的 CA 证书文件。 |
| `--kube-token=TOKEN` | `---->` | 用于认证的 Bearer 令牌，替代 kubeconfig 凭据。 |
| `--kube-tls-server-name=NAME` | `---->` | 验证 API 服务器证书时使用的服务器名称（当其与 URL 主机不同时）。 |
| `--kube-insecure-skip-tls-verify` | `---->` | 跳过 API 服务器证书验证。不安全——仅限受信任/测试环境。 |
| `--burst-limit=INT` | `---->` | 对 API 请求的客户端节流突发上限（默认 `100`）；超大 Chart 可调高。 |
| `--qps=QPS` | `---->` | 与 API 服务器通信的客户端每秒查询数上限；接受小数。 |
| `--registry-config=FILE` | `---->` | OCI 注册表配置（凭据）文件路径。 |
| `--repository-cache=DIR` | `---->` | 用于解析依赖的缓存仓库索引目录。 |
| `--repository-config=FILE` | `---->` | 将仓库名称映射到 URL 的文件路径（`repositories.yaml`）。 |

## 全局选项

同样支持所有[全局选项](index.md)（如 `--debug`、`--unsafe`）。

## 示例

```bash
# 在 "prod" 命名空间安装发布 "my-app" 并等待就绪
kube-kts install /path/to/repository --name my-app -n prod --create-namespace --wait

# 带值覆盖和更长超时的原子安装
kube-kts install . --name my-app --atomic --timeout 10m --set image.tag=1.2.3 -f prod.yaml

# 在不触及集群的情况下预览将安装的内容
kube-kts install ./helm --name my-app --dry-run
```
