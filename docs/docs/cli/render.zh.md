# render

```bash
kube-kts render <REPOSITORY> [TARGET] [-f FILE...]
```

将仓库渲染为磁盘上的标准 Helm Chart。它运行完整流程——扫描、编译、执行——并将结果作为常规 Helm Chart
（包含 `Chart.yaml`、`values.yaml` 以及由纯 YAML 组成的 `templates/` 目录）写入目标目录。输出不含
Kotlin，也不含 Go 模板逻辑，因此可直接被 `helm` 使用、提交、做差异比较或手动查看。

`render` 是 Kube KTS 世界与标准工具之间的桥梁：Helm 支持的命令在内部所做的一切都从这一步开始。
显式运行它是*查看* Kube KTS 产物的最佳方式。

!!! tip "查看输出"
    传入显式的 `TARGET`，使 Chart 在命令结束后得以保留。否则 Chart 会写入操作系统可能清理的临时目录。

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `REPOSITORY` | 是 | 要渲染的仓库路径。若省略，则使用当前工作目录。 |
| `TARGET` | 否 | 写入 Helm Chart 的目录。若省略，则创建临时目录。已存在的目标会被新渲染的 Chart 覆盖。 |

## 选项

除[全局选项](index.md)外：

| 选项 | 标记 | 说明 |
|---|---|---|
| `-f`, `--values=FILE` | `---->` | 渲染期间供 Kotlin 脚本使用的 YAML 值文件。可重复；文件按顺序叠加，后者覆盖前者。每个文件必须存在。（对 `render` 而言，这些值影响生成的 Chart；由于 `render` 不调用 Helm，因此不会传给 Helm。） |

## 示例

```bash
# 将当前目录渲染到 ./out
kube-kts render . ./out

# 用两个叠加的值文件渲染指定仓库
kube-kts render /path/to/repository ./out -f base.yaml -f prod.yaml

# 渲染到临时目录，仅用于检查是否成功
kube-kts render ./helm
```
