package helm

chart("name", "1.0.0") {
    kubeVersion {
        minInclusive("1.0.0")
        maxExclusive("2.0.0")
    }
    description = "description"
    type = ChartSpec.Type.Library

    addKeyword("keyword")

    home = "home"

    addSource("source")

    addDependency("dependency", "1.0.0") {
        repository = URI("https://repo.example.com")
        alias = "alias"
        condition = "condition"

        addTag("tag")
        addPathImportValue("path")
        addMappingImportValue("key", "value")
    }

    addMaintainer("maintainer") {
        email = "email"
        url = URI("https://url.example.com")
    }

    icon = "icon"
    appVersion = "appVersion"
    deprecated = true

    addAnnotation("annotation", "value")
}