# Chart

The following page describes how to use the chart DSL.

## Example

This is a maximal example of a chart:

```kotlin
chart("name", "1.0.0") {
    kubeVersion {
        minInclusive("1.0.0")
        maxExclusive("2.0.0")
    }
    description = "description"
    type = ChartSpec.Type.Library

    addKeyword("keyword")

    home = "home"

    addSource(URI("https://source.example.com"))

    addDependency("dependency", "1.0.0") {
        repository = URI("https://repo.example.com")
        alias = "alias"
        condition = "condition"

        addTag("tag")
        addPathImportValue("path")
        addMappingImportValue("key", "value")
    }

    addMaintainer("maintainer") {
        email = MailAddress.parse("maintainer@mail.com")
        url = URI("https://url.example.com")
    }

    icon = URI("https://icon.example.com")
    appVersion = "appVersion"
    deprecated = true

    addAnnotation("annotation", "value")
}
```

### Special Types

- `ChartSpec.Type`: Represents the type of the chart, such as `Library` or `Application`.
- `MailAddress`: Represents an email address.
- `URI`: Represents a Uniform Resource Identifier.