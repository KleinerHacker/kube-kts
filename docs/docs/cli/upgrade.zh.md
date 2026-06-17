# upgrade

```bash
kube-kts upgrade <REPOSITORY> [TARGET] --name <NAME> [选项]
```

将仓库渲染为 Helm Chart，然后运行 `helm upgrade` 升级 Kubernetes 集群中已有的发布（或在配合
`--install` 时，若不存在则安装）。它是 [`install`](install.md) 的自然对应命令，几乎共享其所有标志，并
另有若干升级专属标志，用于控制如何复用上一次发布的值。

## 工作原理

1. 扫描、编译并将仓库渲染到（临时或显式的）Chart 目录，并应用你的 `-f`/`--set*` 值。
2. 在该目录中执行 `helm upgrade <NAME> .`，并附加所有被转发的选项。
3. Helm 创建新的发布修订；命令的退出码反映 Helm 的结果。失败时，除非传入 `--atomic`，否则不会回滚。

!!! note "发布名称是选项，命名空间是 `-n`"
    发布名称用 `--name` 传入，并作为位置参数 `NAME` 转发给 Helm。`-n` 短选项保留给 `--namespace`，
    以与 Helm 保持一致。

!!! tip "升级或安装与值处理"
    使用 `-i`/`--install` 可在发布尚不存在时进行安装。默认情况下升级使用你传入的值；
    `--reuse-values`、`--reset-values` 和 `--reset-then-reuse-values` 控制如何考虑上一次发布的值。

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `REPOSITORY` | 是 | 用于升级的仓库路径。若省略，则使用当前工作目录。 |
| `TARGET` | 否 | 升级前 Chart 渲染到的目录。若省略，则使用临时目录。 |

## upgrade 选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--name=NAME` | | 要升级的发布名称。作为位置参数 `NAME` 转发给 Helm。 |
| `-i`, `--install` | `---->` | 若同名发布尚不存在，则执行安装而非失败。 |
| `--atomic` | `---->` | 升级失败时回滚本次升级所做的更改。隐含 `--wait`。 |
| `--cleanup-on-fail` | `---->` | 升级失败时允许删除本次升级新创建的资源。 |
| `--create-namespace` | `---->` | 配合 `--install` 时，若发布命名空间不存在则创建。 |
| `--dry-run` | `---->` | 模拟升级，不改变集群。 |
| `--enable-dns` | `---->` | 渲染模板时启用 DNS 查询。 |
| `--force` | `---->` | 通过替换策略强制更新资源。可能具有破坏性。 |
| `--history-max=INT` | `---->` | 限制每个发布保留的修订数（`0` 表示无限制；Helm 默认 `10`）。 |
| `-l`, `--labels=KEY=VALUE` | `---->` | 向发布元数据添加标签。可重复。 |
| `--description=TEXT` | `---->` | 为本次发布修订附加自定义描述。 |
| `-o`, `--output=FORMAT` | `---->` | 输出格式：`table`（默认）、`json` 或 `yaml`。 |
| `--reset-values` | `---->` | 将值重置为 Chart 内置的值，忽略上一次发布。 |
| `--reuse-values` | `---->` | 复用上一次发布的值，并合并新的覆盖项。 |
| `--reset-then-reuse-values` | `---->` | 先重置为 Chart 的值，再应用上一次发布的值并合并覆盖项。 |
| `--take-ownership` | `---->` | 忽略 Helm 所有权注解，接管已有资源。请谨慎使用。 |
| `--wait` | `---->` | 在标记发布成功前，等待所有资源就绪。遵循 `--timeout`。 |
| `--wait-for-jobs` | `---->` | 与 `--wait` 同用时，还等待所有 Job 完成。 |

## 值选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--set=KEY=VALUE` | `---->` | 内联覆盖值，如 `--set image.tag=1.2.3`。类型会被推断。可重复；后面的 `--set` 胜出并覆盖 `-f` 文件。 |
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
| `--pass-credentials` | `---->` | 将仓库凭据发送到所有域。仅限受信任仓库。 |
| `--ca-file=FILE` | `---->` | 用于验证 HTTPS Chart 服务器 TLS 证书的 CA 包。 |
| `--cert-file=FILE` | `---->` | 用于对 Chart 服务器进行双向 TLS 的客户端 TLS 证书。 |
| `--key-file=FILE` | `---->` | 与 `--cert-file` 匹配的客户端 TLS 私钥。 |
| `--insecure-skip-tls-verify` | `---->` | 下载 Chart 时跳过 TLS 验证。不安全——生产环境请避免。 |
| `--keyring=FILE` | `---->` | 用于验证签名 Chart 的公钥钥匙串（与 `--verify` 配合）。默认 `~/.gnupg/pubring.gpg`。 |
| `--verify` | `---->` | 升级前验证 Chart 的来源签名；缺失或无效时失败。 |
| `--version=VERSION` | `---->` | 选择 Chart 版本的 SemVer 约束。默认使用最新稳定版本。 |
| `--devel` | `---->` | 也考虑预发布版本（等同于 `>0.0.0-0`）。 |
| `--dependency-update` | `---->` | 升级前下载/更新缺失的 Chart 依赖。 |

## 渲染选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--no-hooks` | `---->` | 升级期间跳过所有 Chart 生命周期 hooks。 |
| `--disable-openapi-validation` | `---->` | 不根据集群的 OpenAPI 模式校验渲染的清单。 |
| `--name-template=TEMPLATE` | `---->` | 用于计算发布名称的 Go 模板，作为固定 `--name` 的替代。 |
| `--render-subchart-notes` | `---->` | 同时渲染子 Chart 的 NOTES.txt。 |
| `--skip-crds` | `---->` | 不安装 Chart 附带的 CRD。 |
| `--post-renderer=PATH` | `---->` | 在清单被应用前对其运行的可执行文件。 |
| `--post-renderer-args=ARG` | `---->` | 传给 `--post-renderer` 可执行文件的参数。可重复。 |
| `--timeout=DURATION` | `---->` | 等待任一单个 Kubernetes 操作的最长时间，Go 时长格式（默认 `5m0s`）。与 `--wait` 配合时最为相关。 |

## Helm 全局选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | 发布所在的命名空间。默认为当前 kube-context 的命名空间。 |
| `--kube-context=CONTEXT` | `---->` | 要使用的 kubeconfig 上下文，可指向特定集群/用户。 |
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
# 升级发布 "my-app"，若不存在则安装
kube-kts upgrade /path/to/repository --name my-app -i -n prod

# 复用上一次的值、仅覆盖镜像标签的原子升级
kube-kts upgrade . --name my-app --atomic --reuse-values --set image.tag=1.3.0

# 在不触及集群的情况下预览升级
kube-kts upgrade ./helm --name my-app --dry-run
```
