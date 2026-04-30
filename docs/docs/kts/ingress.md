# Ingress

The following page describes how to use KTS to configure Ingress.

## Example 

This is a maximal example of Ingress configuration:

```kotlin
ingress {
    metadata("metadata") {
        ...
    }

    spec {
        ingressClassName = "className"

        defaultServiceBackend("service") {
            port(9999)
        }

        addTls {
            secretName = "secretName"
            addHost("host.example.com")
        }

        addRule {
            host = "rule.example.com"
            addHttpPath(RulesSpec.HttpPathConfig.PathType.Exact) {
                path = "path"
                serviceBackend("ruleService") {
                    port(7777)
                }
            }
        }
    }
}
```