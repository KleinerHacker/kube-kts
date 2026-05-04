# Ingress DSL

Die `ingress` DSL wird verwendet, um Kubernetes Ingress-Ressourcen zu konfigurieren, die den externen Zugriff auf Dienste in einem Cluster verwalten, normalerweise über HTTP.

## Grundlegende Verwendung

Eine minimale Ingress-Konfiguration erfordert `metadata` und mindestens eine Regel im `spec`.

```kotlin
ingress {
    metadata("my-ingress") {
        namespace = "default"
    }

    spec {
        rules {
            rule {
                host = "example.com"
                httpPaths {
                    httpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
                        path = "/"
                        serviceBackend("my-service") {
                            port(80)
                        }
                    }
                }
            }
        }
    }
}
```

## Detailliertes Beispiel

Unten findest du ein umfassendes Beispiel, das verschiedene Konfigurationsoptionen zeigt, einschließlich TLS und mehrerer Regeln.

```kotlin
ingress {
    metadata("full-ingress") {
        namespace = "production"
    }

    spec {
        ingressClassName = "nginx"

        // Standard-Backend, wenn keine Regeln zutreffen
        defaultServiceBackend("default-service") {
            port(8080)
        }

        // TLS-Konfiguration
        tlsList {
            tls {
                secretName = "example-tls-secret"
                hosts {
                    host("example.com")
                    host("api.example.com")
                }
            }
        }

        // Regel für die Hauptwebsite
        rules {
            rule {
                host = "example.com"
                httpPaths {
                    httpPath(RulesSpec.HttpPathConfig.PathType.Exact) {
                        path = "/home"
                        serviceBackend("web-service") {
                            port("http") // Port über Namen referenzieren
                        }
                    }
                }
            }
        }

        // Regel für API mit mehreren Pfaden
        rules {
            rule {
                host = "api.example.com"
                httpPaths {
                    httpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
                        path = "/v1"
                        serviceBackend("api-v1-service") {
                            port(8081)
                        }
                    }
                    httpPath(RulesSpec.HttpPathConfig.PathType.Prefix) {
                        path = "/v2"
                        serviceBackend("api-v2-service") {
                            port(8082)
                        }
                    }
                }
            }
        }
    }
}
```

## Konfigurationsreferenz

### Metadaten (`metadata`)

| Eigenschaft | Typ | Beschreibung |
| :--- | :--- | :--- |
| `name` | `String` | Der Name der Ingress-Ressource (als erstes Argument übergeben). |
| `namespace` | `String?` | Der Namespace für die Ressource. |
| `generateName` | `String?` | Ein optionales Präfix zur Generierung eines eindeutigen Namens. |

### Ingress-Spezifikation (`spec`)

| Eigenschaft / Methode | Beschreibung |
| :--- | :--- |
| `ingressClassName` | Name der IngressClass-Clusterressource. |
| `defaultServiceBackend(name) { ... }` | Legt das Standard-Backend für einen Dienst fest. |
| `defaultResourceBackend(name, kind) { ... }` | Legt das Standard-Backend für eine benutzerdefinierte Ressource fest. |
| `tlsList { tls { ... } }` | Fügt einen TLS-Konfigurationsblock hinzu. (Alternativ: `addTls`) |
| `rules { rule { ... } }` | Fügt eine Ingress-Regel hinzu. (Alternativ: `addRule`) |

### TLS-Konfiguration (`tls`)

| Eigenschaft / Methode | Beschreibung |
| :--- | :--- |
| `secretName` | Der Name des Secrets, das das TLS-Zertifikat und den Schlüssel enthält. |
| `hosts { host(String) }` | Fügt einen Host hinzu, der in das TLS-Zertifikat aufgenommen werden soll. (Alternativ: `addHost`) |

### Regeln (`rule`)

| Eigenschaft / Methode | Beschreibung |
| :--- | :--- |
| `host` | Der vollqualifizierte Domänenname eines Netzwerk-Hosts. |
| `httpPaths { httpPath(type) { ... } }` | Fügt der Regel einen HTTP-Pfad hinzu. (Alternativ: `addHttpPath`) |

### HTTP-Pfad (`httpPath`)

| Eigenschaft / Methode | Beschreibung |
| :--- | :--- |
| `path` | Der Pfad, der gegen den Pfad einer eingehenden Anfrage geprüft wird. |
| `type` | Der Typ des Pfad-Matchings: `Exact`, `Prefix` oder `ImplementationSpecific`. |
| `serviceBackend(name) { ... }` | Verweist auf ein Service-Backend. |
| `resourceBackend(name, kind) { ... }` | Verweist auf ein Backend einer benutzerdefinierten Ressource. |

### Backend-Konfiguration

Wenn `serviceBackend` verwendet wird, muss der Port angegeben werden:
- `port(Int)`: Verwendet einen numerischen Port.
- `port(String)`: Verwendet einen benannten Port.