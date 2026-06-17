# status

```bash
kube-kts status <RELEASE> [选项]
```

运行 `helm status` 以显示已安装发布的状态。与渲染类命令不同，`status` 作用于集群中已存在的发布，因此
**既不需要仓库，也不需要渲染步骤**——调用会直接转发给 Helm。

## 工作原理

1. 不扫描、编译或渲染任何仓库；KTS 脚本在此无关。
2. 执行 `helm status <RELEASE>`，并附加所有被转发的选项。
3. Helm 打印发布状态；命令的退出码反映 Helm 的结果。

!!! note "发布名称是位置参数，命名空间是 `-n`"
    发布名称作为第一个位置参数（`RELEASE`）传入，并原样转发给 Helm。`-n` 短选项保留给 `--namespace`。

!!! note "无需仓库"
    由于 `status` 不渲染任何内容，无需指向 Kube KTS 仓库。这是首个基于无渲染基类
    `BaseDirectHelmCommand` 构建的命令。

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `RELEASE` | 是 | 要查询的发布名称。作为位置参数 `RELEASE` 转发给 Helm。 |

## status 选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--revision=INT` | `---->` | 显示发布指定修订版的状态，而非最新版。 |
| `--output=FORMAT` | `---->` | 以给定格式打印输出：`table`（默认）、`json` 或 `yaml`。 |
| `--show-desc` | `---->` | 额外显示为发布记录的描述信息。 |
| `--show-resources` | `---->` | 额外列出属于该发布的 Kubernetes 资源。 |

## Helm 全局选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | 发布所在的命名空间。默认为当前 kube-context 的命名空间；请显式设置以查询正确的命名空间。 |
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
# 显示 "prod" 命名空间中某个发布的状态
kube-kts status my-app -n prod

# 以 JSON 显示指定修订版
kube-kts status my-app --revision 3 --output json

# 包含发布描述及其资源
kube-kts status my-app --show-desc --show-resources
```
