# registry

```bash
kube-kts registry <SUBCOMMAND> <HOST> [选项]
```

聚合 `helm registry …` 子命令，用于管理对 OCI 注册表的认证。它们作用于本地注册表配置，因此**既不需要仓库，也不需要渲染步骤**——调用会直接转发给 Helm。不带子命令调用 `registry` 会打印用法列表。

## 子命令

| 子命令 | Helm | 说明 |
|---|---|---|
| `registry login <HOST>` | `helm registry login` | 登录 OCI 注册表。 |
| `registry logout <HOST>` | `helm registry logout` | 从 OCI 注册表登出。 |

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `HOST` | 是 | 注册表主机。作为位置参数 `HOST` 转发给 Helm。 |

## 各子命令选项

| 子命令 | 选项（均为 `---->`） |
|---|---|
| `login` | `-u`/`--username=USER`、`-p`/`--password=PASSWORD`、`--password-stdin`、`--insecure`、`--ca-file=FILE`、`--cert-file=FILE`、`--key-file=FILE`、`--plain-http` |
| `logout` | – |

## Helm 全局选项

所有 [Helm 全局选项](status.md#helm-global-options)都会转发给 Helm。

## 全局选项

同样支持所有[全局选项](index.md)。

## 示例

```bash
# 登录注册表（从 stdin 读取密码）
echo "$TOKEN" | kube-kts registry login registry.example.com -u robot --password-stdin

# 登出
kube-kts registry logout registry.example.com
```
