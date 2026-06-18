# env

```bash
kube-kts env [NAME] [选项]
```

运行 `helm env` 以打印 Helm 的环境信息。它纯属信息性，**既不需要仓库，也不需要渲染步骤**——调用会直接转发给 Helm。

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `NAME` | 否 | 要打印的单个环境变量名称。省略时打印所有变量。作为位置参数 `NAME` 转发给 Helm。 |

## Helm 全局选项

所有 [Helm 全局选项](status.md#helm-global-options)都会转发给 Helm。

## 全局选项

同样支持所有[全局选项](index.md)。

## 示例

```bash
# 打印所有 Helm 环境变量
kube-kts env

# 打印单个变量
kube-kts env HELM_BIN
```
