# diff

```bash
kube-kts diff upgrade <REPOSITORY> [TARGET] --name <RELEASE> [选项]
```

聚合基于外部 **`helm-diff` 插件** 的 diff 子命令。它们通过渲染 KTS 仓库并将结果与集群对比，预览某操作将应用的变更。每个子命令都**需要仓库**，并先运行完整的 *扫描 → 编译 → 渲染* 流水线。不带子命令调用 `diff` 会打印用法列表。

!!! note "需要 helm-diff 插件"
    这些命令会调用 `helm diff …`，由外部插件提供。请使用
    `helm plugin install https://github.com/databus23/helm-diff` 安装它。

## 子命令

| 子命令 | Helm | 说明 |
|---|---|---|
| `diff upgrade <REPOSITORY> [TARGET]` | `helm diff upgrade <RELEASE> .` | 预览 `upgrade` 将应用的变更。 |

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `REPOSITORY` | 是 | 要渲染的 Kube KTS 仓库路径。 |
| `TARGET` | 否 | chart 渲染到的目录。省略时使用临时目录。 |

!!! note "发布名称通过 `--name` 传入"
    由于 `REPOSITORY`/`TARGET` 已占用位置参数，发布名称通过 `--name` 传入，并作为位置参数 `RELEASE` 转发给插件。`-n` 短选项保留给 `--namespace`。

## diff 选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--name=NAME` | | 发布名称（作为位置参数 `RELEASE` 转发给插件）。 |
| `--detailed-exitcode` | `---->` | 有变更时返回退出码 `2`。 |
| `--context=NUM` | `---->` | 在变更周围输出 `NUM` 行上下文（`-1` 表示完整上下文）。 |
| `--show-secrets` | `---->` | 不在输出中遮蔽 secret 值。 |
| `--no-hooks` | `---->` | 禁用钩子的 diff。 |
| `--include-tests` | `---->` | 启用 Helm test 钩子的 diff。 |
| `--reset-values` | `---->` | 将值重置为 chart 内置值并合并新值。 |
| `--reuse-values` | `---->` | 复用上次发布的值并合并新值。 |
| `--normalize-manifests` | `---->` | diff 前规范化清单，以排除样式差异。 |

## 值

`diff upgrade` 接受 [`--set` 系列](install.md) 以及通过 [`-f`/`--values`](index.md#values) 的值文件，它们同时供 Kotlin 脚本与插件使用。

## Helm 全局选项

所有 [Helm 全局选项](status.md#helm-global-options)都会转发给 Helm。

## 全局选项

同样支持所有[全局选项](index.md)。

## 示例

```bash
# 预览 upgrade 将应用的变更
kube-kts diff upgrade ./my-repo --name my-app

# 若有任何变更则使构建失败（退出 2）
kube-kts diff upgrade ./my-repo --name my-app --detailed-exitcode
```
