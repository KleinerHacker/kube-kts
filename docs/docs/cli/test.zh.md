# test

```bash
kube-kts test <RELEASE> [选项]
```

运行 `helm test` 以执行发布中定义的测试。它作用于已存在的发布，因此**既不需要仓库，也不需要渲染步骤**——调用会直接转发给 Helm。

## 工作原理

1. 不扫描、编译或渲染任何仓库；KTS 脚本在此无关。
2. 执行 `helm test <RELEASE>`，并附加所有被转发的选项。
3. Helm 运行测试钩子；命令的退出码反映 Helm 的结果。

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `RELEASE` | 是 | 要测试的发布名称。作为位置参数 `RELEASE` 转发给 Helm。 |

## test 选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--filter=KEY=VALUE` | `---->` | 按属性选择测试（如 `name=...`），或用 `!attribute=value` 排除。可重复。 |
| `--hide-notes` | `---->` | 在测试输出中不显示 notes。 |
| `--logs` | `---->` | 所有测试完成后转储测试 Pod 的日志。 |
| `--timeout=DURATION` | `---->` | 等待任一 Kubernetes 操作的时间。 |

## Helm 全局选项

所有 [Helm 全局选项](status.md#helm-global-options)都会转发给 Helm。

## 全局选项

同样支持所有[全局选项](index.md)。

## 示例

```bash
# 运行发布的测试并显示其日志
kube-kts test my-app --logs

# 仅运行特定测试
kube-kts test my-app --filter name=my-test
```
