# history

```bash
kube-kts history <RELEASE> [选项]
```

运行 `helm history` 以显示发布的修订历史。它作用于已存在的发布，因此**既不需要仓库，也不需要渲染步骤**——调用会直接转发给 Helm。

## 工作原理

1. 不扫描、编译或渲染任何仓库；KTS 脚本在此无关。
2. 执行 `helm history <RELEASE>`，并附加所有被转发的选项。
3. Helm 打印修订历史；命令的退出码反映 Helm 的结果。

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `RELEASE` | 是 | 要查询的发布名称。作为位置参数 `RELEASE` 转发给 Helm。 |

## history 选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--max=INT` | `---->` | 历史中包含的最大修订数。 |
| `-o`, `--output=FORMAT` | `---->` | 输出格式：`table`（默认）、`json` 或 `yaml`。 |

## Helm 全局选项

所有 [Helm 全局选项](status.md#helm-global-options)都会转发给 Helm。

## 全局选项

同样支持所有[全局选项](index.md)。

## 示例

```bash
# 显示发布的完整修订历史
kube-kts history my-app

# 以 JSON 显示最近 5 个修订
kube-kts history my-app --max 5 -o json
```
