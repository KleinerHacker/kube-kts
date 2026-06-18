# list

```bash
kube-kts list [选项]
```

运行 `helm list` 以列出发布。与 `status` 一样，`list` 作用于集群，因此**既不需要仓库，也不需要渲染步骤**——调用会直接转发给 Helm。

## 工作原理

1. 不扫描、编译或渲染任何仓库；KTS 脚本在此无关。
2. 执行 `helm list`，并附加所有被转发的选项。
3. Helm 打印发布列表；命令的退出码反映 Helm 的结果。

## list 选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `-a`, `--all` | `---->` | 显示所有发布，不应用任何过滤。 |
| `-A`, `--all-namespaces` | `---->` | 列出所有命名空间中的发布。 |
| `-d`, `--date` | `---->` | 按发布日期排序。 |
| `--deployed` | `---->` | 显示已部署的发布。若未指定其他状态过滤，则自动启用。 |
| `--failed` | `---->` | 显示失败的发布。 |
| `--filter=REGEXP` | `---->` | Perl 兼容正则表达式；仅列出匹配的发布。 |
| `-m`, `--max=INT` | `---->` | 获取发布的最大数量。 |
| `--no-headers` | `---->` | 在默认输出格式中不打印表头。 |
| `--offset=INT` | `---->` | 列表中的下一个发布索引，用于从起点偏移。 |
| `-o`, `--output=FORMAT` | `---->` | 输出格式：`table`（默认）、`json` 或 `yaml`。 |
| `--pending` | `---->` | 显示待处理的发布。 |
| `-r`, `--reverse` | `---->` | 反转排序顺序。 |
| `-l`, `--selector=SELECTOR` | `---->` | 用于过滤的标签查询（支持 `=`、`==`、`!=`）。 |
| `-q`, `--short` | `---->` | 输出简短（安静）列表格式。 |
| `--superseded` | `---->` | 显示被取代的发布。 |
| `--time-format=FORMAT` | `---->` | 使用 Go 时间格式化器格式化时间。 |
| `--uninstalled` | `---->` | 显示已卸载的发布（若卸载时使用了 `--keep-history`）。 |
| `--uninstalling` | `---->` | 显示当前正在卸载的发布。 |

## Helm 全局选项

所有 [Helm 全局选项](status.md#helm-global-options)（如 `-n`/`--namespace`、`--kube-context`）都会转发给 Helm。

## 全局选项

同样支持所有[全局选项](index.md)（如 `--debug`、`--unsafe`）。

## 示例

```bash
# 列出当前命名空间中的发布
kube-kts list

# 以 JSON 列出所有命名空间中的全部发布
kube-kts list -A -o json

# 仅显示失败的发布，最新在前
kube-kts list --failed --date --reverse
```
