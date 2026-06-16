# Chart DSL

`chart` DSL 用于定义 Helm chart 的元数据和依赖项，类似于传统 Helm chart 中的 `Chart.yaml` 文件。

!!! warning "安全性：导入限制"
    默认情况下，KTS 脚本**不允许**使用 `import` 语句或完全限定的类名
    （例如 `java.lang.Runtime`）。只能使用通过预配置默认导入提供的类型。

    使用 `--unsafe` 标志可解除这些限制。

## 基本用法

要定义一个 chart，请使用 `chart` 函数，它以 chart 的**名称**和**版本**作为主要参数。

```kotlin
chart("my-chart", "1.0.0") {
    description = "A short description of my chart"
    type = ChartSpec.Type.Application
}
```

## 详细示例

下面是一个完整示例，展示了 `chart` 块中所有可用的配置选项。

```kotlin
chart("full-featured-chart", "1.2.3") {
    // Metadata
    description = "A comprehensive example of the Chart DSL"
    type = ChartSpec.Type.Library // Default is Application if not specified
    home = "https://github.com/example/kube-kts"
    icon = URI("https://example.com/icon.png")
    appVersion = "2.5.0"
    deprecated = false

    // Keywords and sources
    keywords {
        keyword("kubernetes")
        keyword("kotlin")
        keyword("dsl")
    }
    sources {
        source(URI("https://github.com/example/kube-kts/src"))
    }

    // Compatibility
    kubeVersion {
        minInclusive("1.20.0")
        maxExclusive("1.30.0")
    }

    // Dependencies
    dependencies {
        dependency("common-utils", "0.5.0") {
            repository = URI("https://charts.example.com")
            alias = "utils"
            condition = "utils.enabled"

            tags {
                tag("infrastructure")
            }
            pathImportValues {
                pathImportValue("exports.values")
            }
            mappingImportValues {
                mappingImportValue("source.key", "destination.key")
            }
        }
    }

    // Maintainers
    maintainers {
        maintainer("John Doe") {
            email = MailAddress.parse("john.doe@example.com")
            url = URI("https://johndoe.com")
        }
    }

    // Custom annotations
    annotations {
        annotation("custom-metadata-key", "some-value")
    }
}
```

## 配置参考

### 顶层属性

| 属性 | 类型 | 说明 |
| :--- | :--- | :--- |
| `description` | `String?` | chart 的单行描述。 |
| `type` | `ChartSpec.Type?` | chart 类型：`Application` 或 `Library`。 |
| `home` | `String?` | 项目主页的 URL。 |
| `icon` | `URI?` | 用作图标的 SVG 或 PNG 图片的 URL。 |
| `appVersion` | `String?` | 此 chart 所包含应用的版本（不是 chart 的版本）。 |
| `deprecated` | `Boolean?` | 此 chart 是否已弃用。 |

### 方法

| 方法 | 说明 |
|:-----------------------------------------------------------------| :--- |
| `keywords { ... }` | 添加用于查找此 chart 的关键字。（替代方法：`addKeyword`、`addKeywords`） |
| `sources { ... }` | 添加此项目源代码的 URL。（替代方法：`addSource`、`addSources`） |
| `kubeVersion { ... }` | 设置兼容的 Kubernetes 版本范围。 |
| `dependencies { ... }` | 添加一个 chart 依赖项。参见下文 [依赖项](#dependencies)。（替代方法：`addDependency`） |
| `maintainers { ... }` | 添加维护者信息。（替代方法：`addMaintainer`） |
| `annotations { ... }` | 添加自定义注解。（替代方法：`addAnnotation`） |


### 依赖项

`dependency` 块支持精细化控制：

- `repository`：chart 仓库的 URL。
- `alias`：chart 的别名（当同一个 chart 被多次使用时很有用）。
- `condition`：用于决定是否安装该 chart 的布尔表达式。
- `tags { tag(String) }`：添加用于分组依赖项的标签。（替代方法：`addTag`）
- `pathImportValues { pathImportValue(path) }`：从子 chart 导入值。（替代方法：`addPathImportValue`）
- `mappingImportValues { mappingImportValue(childKey, parentKey) }`：将子 chart 中的特定值映射到父 chart。（替代方法：`addMappingImportValue`）

## 特殊类型

- `ChartSpec.Type`：包含 `Application` 和 `Library` 值的枚举。
- `MailAddress`：用于电子邮件验证和格式化的辅助类型。
- `URI`：用于表示网页链接的标准 `java.net.URI`。
