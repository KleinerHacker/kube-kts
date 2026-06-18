# package

```bash
kube-kts package <REPOSITORY> [TARGET] [选项]
```

将 KTS 仓库渲染为普通 Helm chart，然后对其运行 `helm package .` 以生成带版本的 chart 归档（`.tgz`）。与发布类命令不同，`package` **需要仓库**，并先运行完整的 *扫描 → 编译 → 渲染* 流水线。

## 工作原理

1. 扫描仓库，编译并执行 Kotlin 脚本，并将结果渲染到 `TARGET` 目录（省略时使用临时目录）。
2. 在该目录中执行 `helm package .`，并附加所有被转发的选项。
3. Helm 写出 chart 归档；命令的退出码反映 Helm 的结果。

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `REPOSITORY` | 是 | 要渲染并打包的 Kube KTS 仓库路径。 |
| `TARGET` | 否 | chart 渲染到的目录。省略时使用临时目录。 |

## package 选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--app-version=VERSION` | `---->` | 设置 chart 的 `appVersion`。 |
| `--version=VERSION` | `---->` | 设置 chart 的（semver）`version`。 |
| `-d`, `--destination=DIR` | `---->` | 写入 chart 归档的位置。 |
| `-u`, `--dependency-update` | `---->` | 打包前将依赖从 `Chart.yaml` 更新到 `charts/`。 |
| `--sign` | `---->` | 使用 PGP 私钥对包签名。 |
| `--key=NAME` | `---->` | 签名密钥名称（与 `--sign` 配合）。 |
| `--keyring=FILE` | `---->` | 公钥环位置。 |
| `--pass-stdin` | `---->` | 从 stdin 读取 PGP 口令（与 `--sign` 配合）。 |

## 值

`package` 通过 [`-f`/`--values`](index.md#values) 接受值文件，在渲染期间供 Kotlin 脚本使用。

## Helm 全局选项

所有 [Helm 全局选项](status.md#helm-global-options)都会转发给 Helm。

## 全局选项

同样支持所有[全局选项](index.md)。

## 示例

```bash
# 用显式版本渲染并打包仓库
kube-kts package ./my-repo --version 1.2.3 --app-version 2.0.0 -d ./dist
```
