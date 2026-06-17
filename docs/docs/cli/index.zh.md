# Kube KTS 命令行 (CLI)

Kube KTS 是通过 `kube-kts` 命令执行的命令行工具。它是 Helm 的类型安全封装：不使用 Go 模板，而是
使用 Kotlin 脚本 (KTS) 描述 Kubernetes 资源，将其渲染为标准的 Helm Chart，然后委托给 Helm 执行
实际的集群操作。

当内置的 `--help` 输出不足以解答疑问时，本章是首要参考资料。帮助文本刻意保持简短以便在屏幕上显示；
本章则更进一步，详细说明每个选项的行为、默认值、单位和相互作用。每个命令都有独立页面，记录其
**所有**选项。

## 与 Helm 的关系

Kube KTS 从不替代 Helm，而是为其准备输入。一个典型命令分两个阶段运行：

1. **渲染阶段（Kube KTS）：** 扫描仓库，编译并执行 Kotlin 脚本，将结果作为标准 Helm Chart 写入目标
   目录。
2. **Helm 阶段（委托）：** 对于由 Helm 支持的命令（`lint`、`template`、`install`、`upgrade`、`uninstall`），
   Kube KTS 随后针对渲染出的 Chart 调用真正的 `helm` 可执行文件，并将所有 Helm 专属选项原样转发。

因此，这些命令同时接受 Kube KTS 选项（影响渲染）和 Helm 选项（被转发）。最终传给 Helm 的选项会被
相应标记——参见下方的“选项标记说明”。

!!! note "必须安装 Helm"
    `lint`、`template`、`install`、`upgrade`、`uninstall` 需要 `helm` 可执行文件在 `PATH` 中可用。
    `validate`、`compile`、`render` 无需 Helm 即可工作。

## 用法

```bash
kube-kts [全局选项] <命令> <REPOSITORY> [TARGET] [命令选项]
```

### 仓库与目标路径

- **`REPOSITORY`** 是第一个位置参数，指向 Kube KTS 仓库（包含 `*.spec.kts`、`*.lib.kts`、
  `values.yaml` 等的目录）。若省略，则使用当前工作目录。若路径不存在，命令会立即失败，根本不会调用
  Helm。
- **`TARGET`** 是渲染类命令（`render`、`lint`、`template`、`install`、`upgrade`、`uninstall`）使用的可选第二个
  位置参数，即 Chart 渲染到的目录。若省略，会创建一个临时目录并由操作系统清理；当你想查看或复用生成
  的 Chart 时，请显式传入目标目录。

### 退出码

成功时 CLI 返回 `0`，失败时返回非零退出码（例如仓库无效、渲染失败，或 Helm 返回非零退出码）。
诊断失败时可使用 `--exception` 查看完整堆栈。

## 命令

| 命令 | 页面 | 用途 |
|---|---|---|
| `validate` | [validate](validate.md) | 校验 KTS 仓库，不产生输出。 |
| `compile` | [compile](compile.md) | 将 KTS 仓库编译为对象实例。 |
| `render` | [render](render.md) | 将仓库渲染为磁盘上的标准 Helm Chart。 |
| `lint` | [lint](lint.md) | 渲染后通过 `helm lint` 进行 lint。 |
| `template` | [template](template.md) | 渲染并通过 `helm template` 打印清单。 |
| `install` | [install](install.md) | 渲染并通过 `helm install` 安装到集群。 |
| `upgrade` | [upgrade](upgrade.md) | 渲染并通过 `helm upgrade` 升级（或安装）发布。 |
| `uninstall` | [uninstall](uninstall.md) | 渲染并通过 `helm uninstall` 卸载一个或多个发布。 |
| `status` | [status](status.md) | 通过 `helm status` 显示发布状态（无需渲染）。 |

## 这些命令是否需要渲染？（KTS 相关性）

一个命令是否渲染 KTS 仓库，决定了你的 Kotlin 脚本对该命令是否相关——也就决定了是否需要为该命令提供一个
**仓库**（KTS、纯 YAML 或混合）：

- **需要渲染 → 需要仓库（KTS 相关）：** `validate`、`compile`、`render`、`lint`、`template`、`install`
  和 `upgrade` 都会运行 *扫描 → 编译 → 渲染* 流程，因此依赖你的 KTS 脚本，必须为它们提供仓库。
- **与渲染无关 → 无需仓库（KTS 不相关）：** 针对已安装发布、集群或仓库的操作（例如将来的 `status`、
  `list`、`rollback`）不需要渲染后的 Chart，因此 KTS 脚本对它们不起作用。

!!! note "`uninstall` 是特例"
    `uninstall` 仅按名称移除发布，技术上不需要渲染后的 Chart。出于一致性，它当前在调用 Helm 前仍会
    渲染，但从功能上看 KTS 脚本对它并不相关。

## 选项标记说明

在 `--help` 输出中，有一个专门的列显示每个选项的性质。命令页面的选项表使用相同的标记，使文档与帮助
屏幕完全一致：

| 标记 | 含义 |
|---|---|
| `---->` | 该选项会转发到底层 Helm CLI，其效果与 Helm 完全相同。 |
| `*` | 该选项是实验性的，未来版本可能更改或移除。需要 `--experimental`。 |
| `!!!` | 该选项具有危险性 / 与安全相关。使用前请仔细阅读其说明。 |

没有标记的选项仅供 Kube KTS 内部使用，绝不会到达 Helm。

## 全局选项

这些选项对每个命令都可用，通常放在命令名**之前**。

| 选项 | 标记 | 说明 |
|---|---|---|
| `--debug` | `---->` | 打印调试级诊断信息，包括运行了哪些步骤以及传给 Helm 的确切参数。它还以 `--debug` 转发给 Helm，使 Helm 本身也变得详细。出现异常行为时的首选排查手段。 |
| `--verbose` | | 打印所有信息，包括 trace 级细节（比 `--debug` 更多）。便于逐步跟踪扫描/编译/渲染流程。不转发给 Helm。 |
| `--show-log-level` | | 在每行日志前加上其级别（INFO/DEBUG/…）。启用 `--debug` 或 `--verbose` 时自动生效。 |
| `--exception` | | 出错时打印完整异常堆栈，而非简短友好的信息。报告缺陷或诊断非显而易见的失败时使用。 |
| `--experimental` | | 解锁下文的实验性选项。未设置此项时，传入任何实验性选项都会使命令失败并给出说明。 |
| `--unsafe` | `!!!` | 允许在 Kotlin 脚本中使用 `import` 语句和全限定类名。这会让脚本执行任意 JVM 代码，因此仅对完全信任的仓库启用。 |

## 实验性功能

实验性功能不稳定，未来版本可能更改。它们需要 `--experimental` 标志，否则命令在执行任何工作前就会失败。

| 选项 | 标记 | 说明 |
|---|---|---|
| `--yaml-merge=TYPE` | `*` | 选择合并 YAML 文档（例如基础文件与覆盖文件）所用的算法。`HELM`（默认）复现 Helm 自身的合并语义，使结果与 Helm 一致；`INTERNAL` 使用 Kube KTS 自定义算法，并额外遵循 `--yaml-array-merge`。 |
| `--yaml-array-merge=TYPE` | `*` | 控制数组的合并方式，仅在 `--yaml-merge=INTERNAL` 下生效。`None` 保持基础数组不变，`Replace`（默认）用覆盖数组替换，`AddFirst` 前置覆盖项，`AddLast` 追加覆盖项。纯 Helm 中数组始终被替换，此项即用于改变该行为。 |

## 值 (Values)

所有渲染类命令（`render`、`lint`、`template`、`install`、`upgrade`、`uninstall`）都接受值文件。值作用于两个阶段：
渲染期间供 Kotlin 脚本使用，**并**传递给 Helm。

| 选项 | 标记 | 说明 |
|---|---|---|
| `-f`, `--values=FILE` | `---->` | YAML 值文件。执行 Kotlin 脚本时使用，并额外以 `-f` 转发给 Helm。可重复：多个文件按给定顺序叠加，后面的文件覆盖前面的。内联 `--set*` 覆盖（在支持的命令中）优先级高于所有 `-f` 文件。文件必须存在，否则命令失败。 |
