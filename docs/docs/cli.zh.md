# Kube KTS CLI

Kube KTS 是一个命令行工具，可通过 `kube-kts` 命令执行。

要处理一个仓库，请提供该仓库的路径。如果未提供仓库路径，
则使用当前工作目录。

## 全局选项

- `--debug`：打印调试信息，包括日志级别。
- `--verbose`：打印所有信息，包括日志级别。
- `--show-log-level`：在输出中打印日志级别。
- `--exception`：在出错时打印完整的异常堆栈跟踪。
- `--experimental`：启用实验性功能。
- `--unsafe`：启用不安全模式，允许在 Kotlin 脚本中使用 `import` 语句和完全限定的类名。

## 实验性功能

实验性功能不稳定，可能在未来版本中发生变化。要使用它们，
请启用 `--experimental` 标志。

- `--yaml-merge`：定义用于合并 YAML 文件的算法。
    - `HELM`：使用标准的 Helm 合并算法（默认）。
    - `INTERNAL`：使用自定义的内部合并算法。
- `--yaml-array-merge`：定义使用 `INTERNAL` 算法时数组的合并策略。
    - `None`：保持基础数组不变。
    - `Replace`：用覆盖数组替换基础数组（默认）。
    - `AddFirst`：将覆盖数组添加到基础数组的开头。
    - `AddLast`：将覆盖数组添加到基础数组的末尾。

## Validate（验证）

`kube-kts validate` 命令用于验证仓库。它会检查仓库
是否有效并包含所有必需的文件。

### 参数

1. `REPOSITORY`：仓库路径。如果未提供，则使用当前目录。

### 示例

```bash
# 验证当前目录
kube-kts validate .

# 验证指定的仓库
kube-kts validate /path/to/repository
```

## Compile（编译）

`kube-kts compile` 命令用于编译仓库。它会从仓库生成对象实例，
这些实例将在下一步用于渲染模板。

### 参数

1. `REPOSITORY`：仓库路径。如果未提供，则使用当前目录。

### 示例

```bash
# 编译当前目录
kube-kts compile .

# 编译指定的仓库
kube-kts compile /path/to/repository
```

## Render（渲染）

`kube-kts render` 命令用于渲染仓库。它会从仓库生成 Helm 文件，
以便可以与 Helm 一起使用。

### 参数

1. `REPOSITORY`：仓库路径。如果未提供，则使用当前目录。
2. `TARGET`：渲染 Helm 文件的目标目录路径。如果未提供，则使用临时目录。

### 示例

```bash
# 将当前目录渲染到目标目录
kube-kts render . /path/to/target

# 将指定仓库渲染到目标目录
kube-kts render /path/to/repository /path/to/target
```

## Lint（检查）

`kube-kts lint` 命令使用 Helm 对仓库进行检查（lint）。它会检查
渲染后的 Helm 输出是否有效。

### 参数

1. `REPOSITORY`：仓库路径。如果未提供，则使用当前目录。
2. `TARGET`：在检查前渲染 Helm 文件的目标目录路径。如果未提供，则使用临时目录。

### 示例

```bash
# 先渲染到目标目录，再检查当前目录
kube-kts lint . /path/to/target

# 先渲染到目标目录，再检查指定仓库
kube-kts lint /path/to/repository /path/to/target
```

## Template（模板）

`kube-kts template` 命令打印仓库渲染后的模板。它在内部使用 `helm template`。

### 参数

1. `REPOSITORY`：仓库路径。如果未提供，则使用当前目录。
2. `TARGET`：渲染 Helm 文件的目标目录路径。如果未提供，则使用临时目录。

### 选项

- `-n`、`--name`：**（必填）**要进行 template 的 chart 名称。

### 示例

```bash
# 使用指定名称对当前目录执行 template
kube-kts template . /path/to/target --name my-release

# 使用指定名称对指定仓库执行 template
kube-kts template /path/to/repository /path/to/target -n my-release
```

## Install（安装）

即将推出。

## Uninstall（卸载）

即将推出。
