# search

```bash
kube-kts search <SUBCOMMAND> [KEYWORD] [选项]
```

聚合 `helm search …` 子命令，用于搜索 chart。它们作用于仓库 / Artifact Hub，因此**既不需要仓库，也不需要渲染步骤**——调用会直接转发给 Helm。不带子命令调用 `search` 会打印用法列表。

## 子命令

| 子命令 | Helm | 说明 |
|---|---|---|
| `search repo [KEYWORD]` | `helm search repo` | 搜索你已添加的仓库。 |
| `search hub [KEYWORD]` | `helm search hub` | 搜索 Artifact Hub 或你自己的 hub 实例。 |

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `KEYWORD` | 否 | 要搜索的关键词。作为位置参数 `KEYWORD` 转发给 Helm。 |

## 各子命令选项

| 子命令 | 选项（均为 `---->`） |
|---|---|
| `repo` | `--devel`、`--fail-on-no-result`、`--max-col-width=UINT`、`-o`/`--output=FORMAT`、`-r`/`--regexp`、`--version=VERSION`、`-l`/`--versions` |
| `hub` | `--endpoint=URL`、`--fail-on-no-result`、`--list-repo-url`、`--max-col-width=UINT`、`-o`/`--output=FORMAT` |

## Helm 全局选项

所有 [Helm 全局选项](status.md#helm-global-options)都会转发给 Helm。

## 全局选项

同样支持所有[全局选项](index.md)。

## 示例

```bash
# 在已添加的仓库中搜索 "nginx"，列出所有版本
kube-kts search repo nginx -l

# 搜索 Artifact Hub
kube-kts search hub nginx -o json
```
