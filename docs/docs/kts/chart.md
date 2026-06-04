# Chart DSL

The `chart` DSL is used to define the metadata and dependencies of your Helm chart, similar to the `Chart.yaml` file in traditional Helm charts.

!!! warning "Security: Import Restrictions"
    By default, KTS scripts **do not allow** `import` statements or fully qualified class names
    (e.g. `java.lang.Runtime`). Only types provided via the pre-configured default imports may
    be used.

    Use the `--unsafe` flag to lift these restrictions.

## Basic Usage

To define a chart, use the `chart` function, which takes the chart **name** and **version** as its main arguments.

```kotlin
chart("my-chart", "1.0.0") {
    description = "A short description of my chart"
    type = ChartSpec.Type.Application
}
```

## Detailed Example

The following is a comprehensive example showing all available configuration options within the `chart` block.

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

## Configuration Reference

### Top-Level Properties

| Property | Type | Description |
| :--- | :--- | :--- |
| `description` | `String?` | A one-line description of the chart. |
| `type` | `ChartSpec.Type?` | The chart type: `Application` or `Library`. |
| `home` | `String?` | The URL of the project's homepage. |
| `icon` | `URI?` | A URL to an SVG or PNG image to use as an icon. |
| `appVersion` | `String?` | The version of the application this chart contains (not the chart version). |
| `deprecated` | `Boolean?` | Whether this chart is deprecated. |

### Methods

| Method | Description |
|:-----------------------------------------------------------------| :--- |
| `keywords { ... }` | Adds keywords used to find this chart. (Alternative: `addKeyword`, `addKeywords`) |
| `sources { ... }` | Adds URLs to the source code for this project. (Alternative: `addSource`, `addSources`) |
| `kubeVersion { ... }` | Sets the range of compatible Kubernetes versions. |
| `dependencies { ... }` | Adds a chart dependency. See [Dependencies](#dependencies) below. (Alternative: `addDependency`) |
| `maintainers { ... }` | Adds information about a maintainer. (Alternative: `addMaintainer`) |
| `annotations { ... }` | Adds a custom annotation. (Alternative: `addAnnotation`) |


### Dependencies

The `dependency` block enables fine-grained control:

- `repository`: The URL of the chart repository.
- `alias`: An alias for the chart (useful when the same chart is used multiple times).
- `condition`: A boolean expression to decide whether to install the chart.
- `tags { tag(String) }`: Adds a tag to group dependencies. (Alternative: `addTag`)
- `pathImportValues { pathImportValue(path) }`: Imports values from the sub-chart. (Alternative: `addPathImportValue`)
- `mappingImportValues { mappingImportValue(childKey, parentKey) }`: Maps a specific value from the sub-chart to the parent chart. (Alternative: `addMappingImportValue`)

## Special Types

- `ChartSpec.Type`: Enum with values `Application` and `Library`.
- `MailAddress`: A helper type for email validation and formatting.
- `URI`: Standard `java.net.URI` for representing web links.
