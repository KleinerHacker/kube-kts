# Chart DSL

The `chart` DSL is used to define the metadata and dependencies of your Helm chart, similar to the `Chart.yaml` file in traditional Helm charts.

## Basic Usage

To define a chart, use the `chart` function, which takes the chart **name** and **version** as its primary arguments.

```kotlin
chart("my-chart", "1.0.0") {
    description = "A brief description of my chart"
    type = ChartSpec.Type.Application
}
```

## Detailed Example

Below is a comprehensive example demonstrating all available configuration options within the `chart` block.

```kotlin
chart("full-featured-chart", "1.2.3") {
    // Metadata
    description = "A comprehensive example of the Chart DSL"
    type = ChartSpec.Type.Library // Default is Application if not specified
    home = "https://github.com/example/kube-kts"
    icon = URI("https://example.com/icon.png")
    appVersion = "2.5.0"
    deprecated = false

    // Keywords and Sources
    addKeywords("kubernetes", "kotlin", "dsl")
    addSource(URI("https://github.com/example/kube-kts/src"))

    // Compatibility
    kubeVersion {
        minInclusive("1.20.0")
        maxExclusive("1.30.0")
    }

    // Dependencies
    addDependency("common-utils", "0.5.0") {
        repository = URI("https://charts.example.com")
        alias = "utils"
        condition = "utils.enabled"
        
        addTag("infrastructure")
        addPathImportValue("exports.values")
        addMappingImportValue("source.key", "destination.key")
    }

    // Maintainers
    addMaintainer("John Doe") {
        email = MailAddress.parse("john.doe@example.com")
        url = URI("https://johndoe.com")
    }

    // Custom Annotations
    addAnnotation("custom-metadata-key", "some-value")
}
```

## Configuration Reference

### Top-Level Properties

| Property | Type | Description |
| :--- | :--- | :--- |
| `description` | `String?` | A one-sentence description of the chart. |
| `type` | `ChartSpec.Type?` | The type of chart: `Application` or `Library`. |
| `home` | `String?` | The URL of the project's home page. |
| `icon` | `URI?` | A URL to an SVG or PNG image to be used as an icon. |
| `appVersion` | `String?` | The version of the app that this contains (not the chart version). |
| `deprecated` | `Boolean?` | Whether this chart is deprecated. |

### Methods

| Method | Description |
| :--- | :--- |
| `addKeyword(String)` / `addKeywords(vararg String)` | Adds keywords used to search for this chart. |
| `addSource(URI)` / `addSources(vararg URI)` | Adds URLs to the source code for this project. |
| `kubeVersion { ... }` | Sets the range of compatible Kubernetes versions. |
| `addDependency(name, version) { ... }` | Adds a chart dependency. See [Dependencies](#dependencies) below. |
| `addMaintainer(name) { ... }` | Adds information about a maintainer. |
| `addAnnotation(key, value)` | Adds a custom annotation. |

### Dependencies

The `addDependency` block allows for fine-grained control:

- `repository`: The URL of the chart repository.
- `alias`: An alias for the chart (useful if the same chart is used multiple times).
- `condition`: A boolean expression to decide if the chart should be installed.
- `addTag(String)`: Adds a tag to group dependencies.
- `addPathImportValue(path)`: Imports values from the sub-chart.
- `addMappingImportValue(childKey, parentKey)`: Maps a specific value from the sub-chart to the parent.

## Special Types

- `ChartSpec.Type`: Enum with values `Application` and `Library`.
- `MailAddress`: A helper type for email validation and formatting.
- `URI`: Standard `java.net.URI` for representing web links.