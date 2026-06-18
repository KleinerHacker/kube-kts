# verify

```bash
kube-kts verify <PATH> [选项]
```

运行 `helm verify` 以检查打包好的 chart 是否已签名且有效。它作用于本地 chart 包，因此**既不需要仓库，也不需要渲染步骤**——调用会直接转发给 Helm。

## 工作原理

1. 不扫描、编译或渲染任何仓库；KTS 脚本在此无关。
2. 执行 `helm verify <PATH>`，并附加所有被转发的选项。
3. Helm 验证包；命令的退出码反映 Helm 的结果。

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `PATH` | 是 | 要验证的打包好的 chart 路径。作为位置参数 `PATH` 转发给 Helm。 |

## verify 选项

| 选项 | 标记 | 说明 |
|---|---|---|
| `--keyring=FILE` | `---->` | 包含用于验证的公钥的密钥环。 |

## Helm 全局选项

所有 [Helm 全局选项](status.md#helm-global-options)都会转发给 Helm。

## 全局选项

同样支持所有[全局选项](index.md)。

## 示例

```bash
# 针对密钥环验证打包好的 chart
kube-kts verify my-app-1.0.0.tgz --keyring ~/.gnupg/pubring.gpg
```
