# compile

```bash
kube-kts compile <REPOSITORY> [--unsafe]
```

将 KTS 仓库编译为对象实例。它比 [`validate`](validate.md) 更进一步：实际执行 Kotlin 脚本并构建 Chart
的内存对象模型（chart、deployment、service 等），渲染器随后会将其转换为 YAML。这是确认脚本能否
**编译并执行**正确的合适命令——包括类型错误、空安全违规以及脚本内部抛出的运行时异常——同时尚不写入
任何输出。

由于编译会运行你的脚本，脚本级问题也会在此最先暴露：无效的值查找、失败的 `require(...)`，或引用了
不允许导入的类的脚本。

!!! warning "`--unsafe` 会运行任意代码"
    默认情况下脚本在受限沙箱中运行，禁止 `import` 语句和全限定类名。若脚本需要它们，请添加
    `--unsafe`——但仅对你信任的仓库使用，因为它允许脚本执行任意 JVM 代码。

## 参数

| 参数 | 必需 | 说明 |
|---|---|---|
| `REPOSITORY` | 是 | 要编译的仓库路径。若省略，则使用当前工作目录。路径不存在时立即失败。 |

## 选项

除[全局选项](index.md)外，[`--unsafe`](index.md) 标志在此尤其相关，因为编译正是执行脚本的阶段：

| 选项 | 标记 | 说明 |
|---|---|---|
| `--unsafe` | `!!!` | 允许脚本中使用 `import` 语句和全限定类名，以便使用额外的 JVM 类。会启用任意代码执行——仅对信任的仓库使用。 |

## 示例

```bash
# 编译当前目录
kube-kts compile .

# 编译脚本中使用 import 的仓库
kube-kts compile /path/to/repository --unsafe

# 编译，并在脚本抛出异常时打印完整堆栈
kube-kts compile ./helm --exception
```
