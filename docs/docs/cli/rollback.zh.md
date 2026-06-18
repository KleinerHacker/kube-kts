# rollback

```bash
kube-kts rollback <RELEASE> [REVISION] [选项]
```

运行 `helm rollback` 以将发布回滚到先前的修订版。它作用于已存在的发布，因此**既不需要仓库，也不需要渲染步骤**——调用会直接转发给 Helm。

## 工作原理

1. 不扫描、编译或渲染任何仓库；KTS 脚本在此无关。
2. 执行 `helm rollback <RELEASE> [REVISION]`，并附加所有被转发的选项。
3. Helm 执行回滚；命令的退出码反映 Helm 的结果。

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `RELEASE` | 是 | 要回滚的发布名称。作为位置参数 `RELEASE` 转发给 Helm。 |
| `REVISION` | 否 | 要回滚到的修订版。省略时 Helm 回滚到上一个修订版。 |

## rollback 选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--cleanup-on-fail` | `---->` | 回滚失败时允许删除本次回滚创建的新资源。 |
| `--dry-run` | `---->` | 模拟回滚而不做任何更改。 |
| `--force` | `---->` | 必要时通过删除/重建强制更新资源。 |
| `--history-max=INT` | `---->` | 每个发布保存的最大修订数（`0` 表示无限制）。 |
| `--no-hooks` | `---->` | 回滚期间阻止钩子运行。 |
| `--recreate-pods` | `---->` | 如适用，重启该资源的 Pod。 |
| `--timeout=DURATION` | `---->` | 等待任一 Kubernetes 操作的时间。 |
| `--wait` | `---->` | 在标记发布成功前等待所有资源就绪。 |
| `--wait-for-jobs` | `---->` | 与 `--wait` 配合，还会等待所有 Job 完成。 |

## Helm 全局选项

所有 [Helm 全局选项](status.md#helm-global-options)都会转发给 Helm。

## 全局选项

同样支持所有[全局选项](index.md)。

## 示例

```bash
# 回滚到上一个修订版
kube-kts rollback my-app

# 回滚到修订版 3 并等待就绪
kube-kts rollback my-app 3 --wait
```
