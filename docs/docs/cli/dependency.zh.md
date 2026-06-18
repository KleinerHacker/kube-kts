# dependency

```bash
kube-kts dependency <SUBCOMMAND> <REPOSITORY> [TARGET] [选项]
```

聚合 `helm dependency …` 子命令（别名 `dep`），用于管理 chart 的依赖。每个子命令都**需要仓库**：先运行完整的 *扫描 → 编译 → 渲染* 流水线，然后对渲染后的 chart 操作（`helm dependency <sub> .`）。不带子命令调用 `dependency` 会打印用法列表。

## 子命令

| 子命令 | Helm | 说明 |
|---|---|---|
| `dependency build <REPOSITORY> [TARGET]` | `helm dependency build .` | 根据 `Chart.lock` 重建 `charts/`。 |
| `dependency update <REPOSITORY> [TARGET]` | `helm dependency update .` | 根据 `Chart.yaml` 内容更新 `charts/`。 |
| `dependency list <REPOSITORY> [TARGET]` | `helm dependency list .` | 列出 chart 的依赖。 |

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `REPOSITORY` | 是 | 要渲染的 Kube KTS 仓库路径。 |
| `TARGET` | 否 | chart 渲染到的目录。省略时使用临时目录。 |

## 各子命令选项

| 子命令 | 选项（均为 `---->`） |
|---|---|
| `build` | `--keyring=FILE`、`--skip-refresh`、`--verify` |
| `update` | `--keyring=FILE`、`--skip-refresh`、`--verify` |
| `list` | `--max-col-width=UINT` |

## 值

这些命令通过 [`-f`/`--values`](index.md#values) 接受值文件，在渲染期间供 Kotlin 脚本使用。

## Helm 全局选项

所有 [Helm 全局选项](status.md#helm-global-options)都会转发给 Helm。

## 全局选项

同样支持所有[全局选项](index.md)。

## 示例

```bash
# 渲染并更新 chart 依赖
kube-kts dependency update ./my-repo

# 列出渲染后 chart 的依赖
kube-kts dependency list ./my-repo
```
