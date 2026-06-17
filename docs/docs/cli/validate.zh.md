# validate

```bash
kube-kts validate <REPOSITORY>
```

校验 KTS 仓库。该命令扫描仓库，发现每个 `*.spec.kts`、`*.lib.kts` 以及传统的 YAML/模板文件，检查
仓库结构是否正确、是否包含所有必需文件（例如 Chart 定义）。它**不会**编译或执行脚本，也不产生任何
输出工件——这是纯粹的快速结构检查。

可将其作为 CI 流水线中第一道、最廉价的关卡：它能在更昂贵的编译/渲染步骤之前就发现缺失或放错位置的
文件，且从不触及集群或 Helm。

!!! tip "validate 的定位"
    `validate` 仅检查*结构*。若还要验证 Kotlin 脚本能否编译和执行，请使用 [`compile`](compile.md)；
    若还要生成 Helm YAML，请使用 [`render`](render.md)。

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `REPOSITORY` | 是 | 要校验的仓库路径。若省略，则使用当前工作目录。路径不存在时命令立即失败。 |

## 选项

此命令仅接受[全局选项](index.md)。这里最有用的是 `--verbose`（查看究竟发现了哪些文件）和
`--exception`（在校验因非显而易见的原因失败时获取堆栈）。

## 示例

```bash
# 校验当前目录
kube-kts validate .

# 校验指定仓库并显示发现的每个文件
kube-kts validate /path/to/repository --verbose

# 在 CI 中校验并在失败时打印完整堆栈
kube-kts validate ./helm --exception
```
