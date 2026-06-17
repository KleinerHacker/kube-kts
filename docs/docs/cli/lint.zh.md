# lint

```bash
kube-kts lint <REPOSITORY> [TARGET] [选项]
```

将仓库渲染为 Helm Chart，然后对其运行 `helm lint`。Lint 会检查渲染后的 Chart 是否存在问题和违反最佳
实践之处——缺失的必填字段、格式错误的 `Chart.yaml`、无法产生有效 YAML 的模板、建议但缺失的元数据
等等——并以信息、警告或错误的形式报告。

由于 Kube KTS 先进行渲染，`lint` 校验的是 Helm 实际将收到的*真实* Chart，且你的值已经应用。它是
[`compile`](compile.md) 之后的自然下一步：compile 证明脚本可运行，lint 证明生成的 Chart 健全。

## 工作原理

1. 扫描、编译并将仓库渲染到（临时或显式的）Chart 目录。
2. 在该目录中执行 `helm lint .`，并附加所有被转发的选项。
3. 打印 Kube KTS 与 Helm 的合并输出；命令的退出码反映 Helm 的结果。

!!! tip "让构建在警告时失败"
    默认情况下 `helm lint` 仅在错误时失败。添加 `--strict` 可使其在警告时也失败，这通常正是 CI 所需。

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `REPOSITORY` | 是 | 要 lint 的仓库路径。若省略，则使用当前工作目录。 |
| `TARGET` | 否 | lint 前 Chart 渲染到的目录。若省略，则使用临时目录。 |

## lint 选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--quiet` | `---->` | 抑制信息性消息，仅打印警告和错误。适合简洁的 CI 日志。 |
| `--strict` | `---->` | 将警告视为失败，任何 lint 警告都会产生非零退出码。推荐用于不应放过样式/最佳实践问题的流水线。 |
| `--with-subcharts` | `---->` | 除顶层 Chart 外，也 lint 其依赖的子 Chart，而不仅是父 Chart。 |

## 值选项

这些用于设置或覆盖 Chart 的值，并转发给 Helm。它们与 [`-f`/`--values`](index.md) 文件互补。

| 选项 | 标记 | 说明 |
|---|---|---|
| `--set=KEY=VALUE` | `---->` | 内联覆盖值，如 `--set image.tag=1.2.3`。类型会被推断。可重复；后面的 `--set` 胜出并覆盖 `-f` 文件。 |
| `--set-string=KEY=VALUE` | `---->` | 与 `--set` 类似，但值始终保持为字符串（如保留 `"01"`）。可重复。 |
| `--set-file=KEY=PATH` | `---->` | 将 `PATH` 处文件的全部内容作为 `KEY` 的值（适合证书或脚本）。可重复。 |
| `--set-json=KEY=JSON` | `---->` | 从 JSON 表达式设置值，可表示对象和数组，如 `--set-json 'ports=[80,443]'`。可重复。 |
| `--set-literal=KEY=VALUE` | `---->` | 完全按所写设置值，不做任何类型转换。可重复。 |
| `-f`, `--values=FILE` | `---->` | 用于渲染并转发给 Helm 的 YAML 值文件。可重复且按顺序叠加。 |

## Helm 全局选项

这些用于配置与集群的连接和 Helm 环境，并原样转发给 Helm。（对 `lint` 而言，与集群相关的选项通常无关
紧要，因为 lint 是离线的，但为保持一致仍可接受。）

| 选项 | 标记 | 说明 |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | 请求假定的命名空间范围。默认为当前 kube-context 的命名空间。 |
| `--kube-context=CONTEXT` | `---->` | 要使用的 kubeconfig 上下文，可在不切换当前上下文的情况下指向特定集群/用户。 |
| `--kubeconfig=FILE` | `---->` | 要使用的 kubeconfig 文件路径，替代 `$KUBECONFIG` / `~/.kube/config`。 |
| `--kube-apiserver=ADDRESS` | `---->` | 覆盖 kubeconfig 中的 API 服务器地址和端口。 |
| `--kube-as-user=USER` | `---->` | 模拟该用户（RBAC 用户模拟）；需要模拟权限。 |
| `--kube-as-group=GROUP` | `---->` | 模拟该用户组。可重复以模拟多个组。 |
| `--kube-ca-file=FILE` | `---->` | 用于验证 API 服务器 TLS 证书的 CA 证书文件。 |
| `--kube-token=TOKEN` | `---->` | 用于认证的 Bearer 令牌，替代 kubeconfig 凭据。 |
| `--kube-tls-server-name=NAME` | `---->` | 验证 API 服务器证书时使用的服务器名称（当其与 URL 主机不同时）。 |
| `--kube-insecure-skip-tls-verify` | `---->` | 跳过 API 服务器证书验证。不安全——仅限受信任/测试环境。 |
| `--burst-limit=INT` | `---->` | 对 API 请求的客户端节流突发上限（默认 `100`）。 |
| `--qps=QPS` | `---->` | 与 API 服务器通信的客户端每秒查询数上限；接受小数。 |
| `--registry-config=FILE` | `---->` | OCI 注册表配置（凭据）文件路径。 |
| `--repository-cache=DIR` | `---->` | 用于解析依赖的缓存仓库索引目录。 |
| `--repository-config=FILE` | `---->` | 将仓库名称映射到 URL 的文件路径（`repositories.yaml`）。 |

## 全局选项

同样支持所有[全局选项](index.md)（如 `--debug`、`--unsafe`）。

## 示例

```bash
# lint 当前目录
kube-kts lint .

# 带额外值文件和内联覆盖的严格 lint
kube-kts lint /path/to/repository ./out --strict -f prod.yaml --set image.tag=1.2.3

# 安静地 lint，包含子 Chart
kube-kts lint ./helm --with-subcharts --quiet
```
