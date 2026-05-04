# Chart DSL

Die `chart` DSL wird verwendet, um die Metadaten und Abhängigkeiten deines Helm-Charts zu definieren, ähnlich der `Chart.yaml` Datei in traditionellen Helm-Charts.

## Grundlegende Verwendung

Um ein Chart zu definieren, verwende die `chart` Funktion, die den Chart-**Namen** und die **Version** als Hauptargumente entgegennimmt.

```kotlin
chart("my-chart", "1.0.0") {
    description = "Eine kurze Beschreibung meines Charts"
    type = ChartSpec.Type.Application
}
```

## Detailliertes Beispiel

Unten findest du ein umfassendes Beispiel, das alle verfügbaren Konfigurationsoptionen innerhalb des `chart` Blocks zeigt.

```kotlin
chart("full-featured-chart", "1.2.3") {
    // Metadaten
    description = "Ein umfassendes Beispiel der Chart-DSL"
    type = ChartSpec.Type.Library // Standard ist Application, falls nicht angegeben
    home = "https://github.com/example/kube-kts"
    icon = URI("https://example.com/icon.png")
    appVersion = "2.5.0"
    deprecated = false

    // Schlagworte und Quellen
    keywords {
        keyword("kubernetes")
        keyword("kotlin")
        keyword("dsl")
    }
    sources {
        source(URI("https://github.com/example/kube-kts/src"))
    }

    // Kompatibilität
    kubeVersion {
        minInclusive("1.20.0")
        maxExclusive("1.30.0")
    }

    // Abhängigkeiten
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

    // Benutzerdefinierte Annotationen
    annotations {
        annotation("custom-metadata-key", "some-value")
    }
}
```

## Konfigurationsreferenz

### Top-Level Eigenschaften

| Eigenschaft | Typ | Beschreibung |
| :--- | :--- | :--- |
| `description` | `String?` | Eine einzeilige Beschreibung des Charts. |
| `type` | `ChartSpec.Type?` | Der Typ des Charts: `Application` oder `Library`. |
| `home` | `String?` | Die URL der Homepage des Projekts. |
| `icon` | `URI?` | Eine URL zu einem SVG- oder PNG-Bild, das als Icon verwendet werden soll. |
| `appVersion` | `String?` | Die Version der Anwendung, die dieses Chart enthält (nicht die Chart-Version). |
| `deprecated` | `Boolean?` | Ob dieses Chart veraltet ist. |

### Methoden

| Methode                                                           | Beschreibung |
|:-----------------------------------------------------------------| :--- |
| `keywords { ... }`                                          | Fügt Keywords hinzu, die zur Suche nach diesem Chart verwendet werden. (Alternativ: `addKeyword`, `addKeywords`) |
| `sources { ... }`                                           | Fügt URLs zum Quellcode für dieses Projekt hinzu. (Alternativ: `addSource`, `addSources`)          |
| `kubeVersion { ... }`                                            | Legt den Bereich der kompatiblen Kubernetes-Versionen fest. |
| `dependencies { ... }`                                       | Fügt eine Chart-Abhängigkeit hinzu. Siehe [Abhängigkeiten](#abhängigkeiten) unten. (Alternativ: `addDependency`) |
| `maintainers { ... }`                                        | Fügt Informationen über einen Maintainer hinzu. (Alternativ: `addMaintainer`)                    |
| `annotations { ... }`                                        | Fügt eine benutzerdefinierte Annotation hinzu. (Alternativ: `addAnnotation`)                      |



### Abhängigkeiten

Der `dependency` Block ermöglicht eine fein abgestufte Steuerung:

- `repository`: Die URL des Chart-Repositorys.
- `alias`: Ein Alias für das Chart (nützlich, wenn dasselbe Chart mehrmals verwendet wird).
- `condition`: Ein boolescher Ausdruck, um zu entscheiden, ob das Chart installiert werden soll.
- `tags { tag(String) }`: Fügt ein Tag hinzu, um Abhängigkeiten zu gruppieren. (Alternativ: `addTag`)
- `pathImportValues { pathImportValue(path) }`: Importiert Werte aus dem Sub-Chart. (Alternativ: `addPathImportValue`)
- `mappingImportValues { mappingImportValue(childKey, parentKey) }`: Ordnet einen bestimmten Wert aus dem Sub-Chart dem übergeordneten Chart zu. (Alternativ: `addMappingImportValue`)

## Spezielle Typen

- `ChartSpec.Type`: Enum mit den Werten `Application` und `Library`.
- `MailAddress`: Ein Hilfstyp für die E-Mail-Validierung und -Formatierung.
- `URI`: Standard `java.net.URI` zur Darstellung von Web-Links.