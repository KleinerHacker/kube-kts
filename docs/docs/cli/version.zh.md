# version

```bash
kube-kts version [选项]
```

运行 `helm version` 以打印 Helm 版本信息。它纯属信息性，**既不需要仓库，也不需要渲染步骤**——调用会直接转发给 Helm。

## version 选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--short` | `---->` | 仅打印版本号。 |
| `--template=TEMPLATE` | `---->` | 用于版本字符串格式的 Go 模板。 |

## Helm 全局选项

所有 [Helm 全局选项](status.md#helm-global-options)都会转发给 Helm。

## 全局选项

同样支持所有[全局选项](index.md)。

## 示例

```bash
# 打印完整的 Helm 版本
kube-kts version

# 仅打印版本号
kube-kts version --short
```
