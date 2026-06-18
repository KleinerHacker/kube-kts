# repo

```bash
kube-kts repo <SUBCOMMAND> [ARGS] [选项]
```

聚合 `helm repo …` 子命令，用于管理 chart 仓库。它们作用于本地 Helm 配置，因此**既不需要仓库，也不需要渲染步骤**——调用会直接转发给 Helm。不带子命令调用 `repo` 会打印用法列表。

## 子命令

| 子命令 | Helm | 说明 |
|---|---|---|
| `repo add <NAME> <URL>` | `helm repo add` | 添加 chart 仓库。 |
| `repo update [REPO...]` | `helm repo update` | 更新指定（或所有）仓库的本地缓存。 |
| `repo list` | `helm repo list` | 列出已配置的 chart 仓库。 |
| `repo remove <REPO...>` | `helm repo remove` | 移除一个或多个 chart 仓库。 |

## 各子命令选项

| 子命令 | 选项（均为 `---->`） |
|---|---|
| `add` | `--username=USER`、`--password=PASSWORD`、`--pass-credentials`、`--ca-file=FILE`、`--cert-file=FILE`、`--key-file=FILE`、`--insecure-skip-tls-verify`、`--no-update`、`--force-update`、`--allow-deprecated-repos` |
| `update` | `--fail-on-repo-update-fail` |
| `list` | `-o`/`--output=FORMAT` |
| `remove` | – |

## Helm 全局选项

所有 [Helm 全局选项](status.md#helm-global-options)都会转发给 Helm。

## 全局选项

同样支持所有[全局选项](index.md)。

## 示例

```bash
# 添加 Bitnami 仓库
kube-kts repo add bitnami https://charts.bitnami.com/bitnami

# 更新所有仓库
kube-kts repo update

# 以 JSON 列出仓库
kube-kts repo list -o json

# 移除仓库
kube-kts repo remove bitnami
```
