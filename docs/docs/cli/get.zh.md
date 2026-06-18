# get

```bash
kube-kts get <SUBCOMMAND> <RELEASE> [选项]
```

聚合 `helm get …` 子命令，用于下载已安装发布的扩展信息。每个子命令都作用于已存在的发布，因此**既不需要仓库，也不需要渲染步骤**——调用会直接转发给 Helm。不带子命令调用 `get` 会打印用法列表。

## 子命令

| 子命令 | Helm | 说明 |
|---|---|---|
| `get all <RELEASE>` | `helm get all` | 下载发布的全部信息。 |
| `get values <RELEASE>` | `helm get values` | 下载发布的（提供或计算的）值。 |
| `get manifest <RELEASE>` | `helm get manifest` | 下载发布的清单。 |
| `get hooks <RELEASE>` | `helm get hooks` | 下载发布的全部钩子。 |
| `get notes <RELEASE>` | `helm get notes` | 下载发布的 notes。 |
| `get metadata <RELEASE>` | `helm get metadata` | 下载发布的元数据。 |

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `RELEASE` | 是 | 要查询的发布名称。作为位置参数 `RELEASE` 转发给 Helm。 |

## 各子命令选项

| 子命令 | 选项（均为 `---->`） |
|---|---|
| `all` | `--revision=INT`、`--template=TEMPLATE`、`-o`/`--output=FORMAT` |
| `values` | `-a`/`--all`、`--revision=INT`、`-o`/`--output=FORMAT` |
| `manifest` | `--revision=INT` |
| `hooks` | `--revision=INT` |
| `notes` | `--revision=INT` |
| `metadata` | `-o`/`--output=FORMAT` |

`--revision` 选择特定修订版；`values` 上的 `-a`/`--all` 转储所有计算后的值；`--template` 应用 Go 模板；`-o`/`--output` 选择 `table`/`json`/`yaml`。

## Helm 全局选项

所有 [Helm 全局选项](status.md#helm-global-options)都会转发给 Helm。

## 全局选项

同样支持所有[全局选项](index.md)。

## 示例

```bash
# 以 JSON 获取发布的所有计算后的值
kube-kts get values my-app --all -o json

# 获取特定修订版的渲染清单
kube-kts get manifest my-app --revision 2
```
