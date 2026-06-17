# uninstall

```bash
kube-kts uninstall <REPOSITORY> [TARGET] --name <RELEASE> [--name <RELEASE>...] [选项]
```

渲染仓库，然后运行 `helm uninstall` 从集群中移除一个或多个发布。卸载会删除属于某个发布的资源，并且
默认连同其历史一起删除。与其他由 Helm 支持的命令一样，它通过你的 kube-context 与集群通信，并转发所有
受支持的 Helm uninstall 标志。

## 工作原理

1. 扫描、编译并渲染仓库（渲染步骤使工作流与其他命令保持一致；渲染出的 Chart 用作工作目录）。
2. 执行 `helm uninstall <RELEASE>...`，并附加所有被转发的选项。
3. Helm 移除每个发布；命令的退出码反映 Helm 的结果。

!!! note "发布名称是选项，命名空间是 `-n`"
    发布名称用 `--name`（可重复）传入，并作为位置参数 `RELEASE` 转发给 Helm。`-n` 短选项保留给
    `--namespace`。至少需要一个 `--name`。

!!! warning "这会删除集群资源"
    卸载具有破坏性。请先用 `--dry-run` 预览；若想保留发布历史以便日后查看或回滚，请使用 `--keep-history`。

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `REPOSITORY` | 是 | 仓库路径。若省略，则使用当前工作目录。 |
| `TARGET` | 否 | Chart 渲染到的目录。若省略，则使用临时目录。 |

## uninstall 选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--name=RELEASE` | | 要卸载的发布。可重复以在一次调用中移除多个发布；每个都作为位置参数 `RELEASE` 转发给 Helm。**至少需要一个。** |
| `--cascade=STRING` | `---->` | 依赖对象的删除方式：`background`（默认，后台删除）、`foreground`（先等待依赖项）或 `orphan`（保留依赖项）。 |
| `--description=TEXT` | `---->` | 为卸载操作记录的自定义可读描述。 |
| `--dry-run` | `---->` | 模拟卸载并显示将移除的内容，但不改变集群。 |
| `--ignore-not-found` | `---->` | 将缺失的发布视为成功而非错误。使命令幂等——在清理脚本中很方便。 |
| `--keep-history` | `---->` | 删除资源并将发布标记为已删除，但保留其历史以便日后查看或回滚。 |
| `--no-hooks` | `---->` | 卸载期间跳过 pre/post-delete hooks。 |
| `--timeout=DURATION` | `---->` | 等待任一单个 Kubernetes 操作的最长时间，Go 时长格式（默认 `5m0s`）。与 `--wait` 相关。 |
| `--wait` | `---->` | 在返回前阻塞，直到发布的所有资源确实被删除。 |

## Helm 全局选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | 发布所在的命名空间。默认为当前 kube-context 的命名空间；请显式设置以避免从错误的命名空间卸载。 |
| `--kube-context=CONTEXT` | `---->` | 要使用的 kubeconfig 上下文，可在不切换当前上下文的情况下指向特定集群/用户。 |
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
# 从 "prod" 命名空间卸载单个发布
kube-kts uninstall /path/to/repository --name my-app -n prod

# 卸载多个发布并保留其历史
kube-kts uninstall . --name my-app --name my-worker --keep-history

# 预览卸载并将缺失的发布视为成功
kube-kts uninstall ./helm --name my-app --dry-run --ignore-not-found
```
