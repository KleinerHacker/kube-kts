# show

```bash
kube-kts show <SUBCOMMAND> <CHART> [选项]
```

聚合 `helm show …` 子命令（别名 `inspect`），用于显示 chart 信息。它们作用于远程/本地 chart，因此**既不需要仓库，也不需要渲染步骤**——调用会直接转发给 Helm。不带子命令调用 `show` 会打印用法列表。

## 子命令

| 子命令 | Helm | 说明 |
|---|---|---|
| `show all <CHART>` | `helm show all` | 显示 chart 的全部信息。 |
| `show chart <CHART>` | `helm show chart` | 显示 chart 定义（`Chart.yaml`）。 |
| `show values <CHART>` | `helm show values` | 显示 chart 的默认值（`values.yaml`）。 |
| `show readme <CHART>` | `helm show readme` | 显示 chart 的 README。 |
| `show crds <CHART>` | `helm show crds` | 显示 chart 的 CRD。 |

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `CHART` | 是 | 要查看的 chart 引用。作为位置参数 `CHART` 转发给 Helm。 |

## 选项

每个子命令都接受 [chart 下载选项](pull.md#chart-下载选项)（`--repo`、`--version`、凭据、TLS、`--verify` 等）。此外，`show values` 接受：

| 选项 | 标记 | 说明 |
|---|---|---|
| `--jsonpath=EXPRESSION` | `---->` | 用于过滤输出的 JSONPath 表达式。 |

## Helm 全局选项

所有 [Helm 全局选项](status.md#helm-global-options)都会转发给 Helm。

## 全局选项

同样支持所有[全局选项](index.md)。

## 示例

```bash
# 显示仓库中某 chart 的默认值
kube-kts show values bitnami/nginx --version 15.0.0

# 通过 JSONPath 仅显示值中的 image
kube-kts show values bitnami/nginx --jsonpath '{.image}'
```
