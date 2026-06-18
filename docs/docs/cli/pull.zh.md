# pull

```bash
kube-kts pull <CHART> [选项]
```

运行 `helm pull` 从仓库下载 chart。它作用于远程 chart，因此**既不需要仓库，也不需要渲染步骤**——调用会直接转发给 Helm。

## 工作原理

1. 不扫描、编译或渲染任何仓库；KTS 脚本在此无关。
2. 执行 `helm pull <CHART>`，并附加所有被转发的选项。
3. Helm 下载 chart；命令的退出码反映 Helm 的结果。

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `CHART` | 是 | 要下载的 chart 引用。作为位置参数 `CHART` 转发给 Helm。 |

## pull 选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `-d`, `--destination=DIR` | `---->` | 写入 chart 的位置。与 `--untardir` 同时给出时组合使用。 |
| `--prov` | `---->` | 获取 provenance 文件但不验证。 |
| `--untar` | `---->` | 下载后解压 chart。 |
| `--untardir=DIR` | `---->` | 设置 `--untar` 时 chart 解压到的目录。 |

## chart 下载选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--repo=URL` | `---->` | 定位 chart 的仓库 URL。 |
| `--username=USER`、`--password=PASSWORD` | `---->` | 仓库凭据。 |
| `--pass-credentials` | `---->` | 将凭据传递给所有域。 |
| `--ca-file=FILE`、`--cert-file=FILE`、`--key-file=FILE` | `---->` | 下载所用的 TLS 材料。 |
| `--insecure-skip-tls-verify` | `---->` | 跳过下载的 TLS 证书检查。 |
| `--plain-http` | `---->` | 下载使用不安全的 HTTP。 |
| `--keyring=FILE` | `---->` | 用于 `--verify` 的公钥。 |
| `--verify` | `---->` | 使用前验证包。 |
| `--version=VERSION` | `---->` | 要使用的 chart 版本约束。 |
| `--devel` | `---->` | 允许开发版本（等同于 `>0.0.0-0`）。 |

## Helm 全局选项

所有 [Helm 全局选项](status.md#helm-global-options)都会转发给 Helm。

## 全局选项

同样支持所有[全局选项](index.md)。

## 示例

```bash
# 下载并解压特定 chart 版本
kube-kts pull bitnami/nginx --version 15.0.0 --untar -d ./charts
```
