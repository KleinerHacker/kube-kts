chart("name", "1.0.0") {
    kubeVersion {
        minInclusive("1.0.0")
        maxExclusive("2.0.0")
    }
    description = "description"
    type = ChartSpec.Type.Library

    keywords {
        keyword("keyword")
    }

    home = "home"

    sources {
        source(URI("https://source.example.com"))
    }

    dependencies {
        dependency("dependency", "1.0.0") {
            repository = URI("https://repo.example.com")
            alias = "alias"
            condition = "condition"

            tags {
                tag("tag")
            }
            pathImportValues {
                pathImportValue("path")
            }
            mappingImportValues {
                mappingImportValue("key", "value")
            }
        }
    }

    maintainers {
        maintainer("maintainer") {
            email = MailAddress.parse("maintainer@mail.com")
            url = URI("https://url.example.com")
        }
    }

    icon = URI("https://icon.example.com")
    appVersion = "appVersion"
    deprecated = true

    annotations {
        annotation("annotation", "value")
    }
}