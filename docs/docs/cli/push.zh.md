# push

```bash
kube-kts push <CHART> <REMOTE> [选项]
```

运行 `helm push` 将打包好的 chart 上传到远程（OCI）注册表。它作用于已有的 chart 包，因此**既不需要仓库，也不需要渲染步骤**——调用会直接转发给 Helm。

## 工作原理

1. 不扫描、编译或渲染任何仓库；KTS 脚本在此无关。
2. 执行 `helm push <CHART> <REMOTE>`，并附加所有被转发的选项。
3. Helm 上传 chart；命令的退出码反映 Helm 的结果。

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `CHART` | 是 | 打包好的 chart 路径（`.tgz`）。作为位置参数 `CHART` 转发给 Helm。 |
| `REMOTE` | 是 | 远程注册表引用。作为位置参数 `REMOTE` 转发给 Helm。 |

## push 选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--ca-file=FILE` | `---->` | 验证注册表 TLS 证书的 CA 包。 |
| `--cert-file=FILE` | `---->` | 标识客户端的 SSL 证书文件。 |
| `--key-file=FILE` | `---->` | 标识客户端的 SSL 密钥文件。 |
| `--insecure-skip-tls-verify` | `---->` | 跳过上传的 TLS 证书检查。 |
| `--plain-http` | `---->` | 上传使用不安全的 HTTP。 |

## Helm 全局选项

所有 [Helm 全局选项](status.md#helm-global-options)都会转发给 Helm。

## 全局选项

同样支持所有[全局选项](index.md)。

## 示例

```bash
# 将打包好的 chart 推送到 OCI 注册表
kube-kts push my-app-1.0.0.tgz oci://registry.example.com/charts
```
